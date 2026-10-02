package com.example.test.swing;



import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;

import com.example.test.model.OrderModel;
import com.example.test.swing.api.OrderApiClient;

public class OrderMainFrame extends JFrame {

  
	private static final long serialVersionUID = -1146596874249662800L;

	private final ResourceBundle messages = ResourceBundle.getBundle("messages", Locale.forLanguageTag("pt-BR"));

    private JTextField txtProduct;
    private JTextField txtQuantity;
    private JButton btnSendOrder;
    private JTable tblOrders;
    private DefaultTableModel tableModel;

    private final OrderApiClient apiClient = new OrderApiClient();
    private final Map<UUID, OrderModel> trackedOrders = new ConcurrentHashMap<>();

    public OrderMainFrame() {
        initUI();
        startPollingWorker();
    }

    private void initUI() {
        setTitle(messages.getString("app.title"));
        setSize(750, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Form Panel
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 8, 8));
        formPanel.setBorder(BorderFactory.createTitledBorder(messages.getString("panel.new_order.title")));

        formPanel.add(new JLabel(messages.getString("label.product")));
        txtProduct = new JTextField();
        formPanel.add(txtProduct);

        formPanel.add(new JLabel(messages.getString("label.quantity")));
        txtQuantity = new JTextField();
        formPanel.add(txtQuantity);

        btnSendOrder = new JButton(messages.getString("button.send"));
        formPanel.add(new JLabel());
        formPanel.add(btnSendOrder);

        add(formPanel, BorderLayout.NORTH);

        // Table Panel
        String[] columns = {
                messages.getString("table.col.order_id"),
                messages.getString("table.col.product"),
                messages.getString("table.col.quantity"),
                messages.getString("table.col.status")
        };
        tableModel = new DefaultTableModel(columns, 0);
        tblOrders = new JTable(tableModel);
        add(new JScrollPane(tblOrders), BorderLayout.CENTER);

        btnSendOrder.addActionListener(e -> processOrderSubmission());
    }

    private void processOrderSubmission() {
        String product = txtProduct.getText().trim();
        String quantityText = txtQuantity.getText().trim();

        if (product.isEmpty() || quantityText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    messages.getString("validation.fill_all"),
                    messages.getString("validation.title"),
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityText);
            if (quantity <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    messages.getString("validation.invalid_quantity"),
                    messages.getString("validation.title"),
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        UUID orderId = UUID.randomUUID();
        String initialStatus = messages.getString("status.sent_waiting");
        OrderModel order = new OrderModel(orderId, product, quantity, initialStatus);

        trackedOrders.put(orderId, order);
        tableModel.addRow(new Object[]{orderId, product, quantity, order.getStatus()});

        txtProduct.setText("");
        txtQuantity.setText("");

        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                try {
                    return apiClient.sendOrder(orderId, product, quantity);
                } catch (Exception ex) {
                    return false;
                }
            }

            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (!success) {
                        updateOrderStatusUI(orderId, messages.getString("status.connection_failed"));
                        JOptionPane.showMessageDialog(OrderMainFrame.this,
                                messages.getString("error.network.message"),
                                messages.getString("error.network.title"),
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    updateOrderStatusUI(orderId, messages.getString("status.connection_failed"));
                }
            }
        }.execute();
    }

    private void startPollingWorker() {
        Timer timer = new Timer(3000, e -> {
            new SwingWorker<Void, OrderModel>() {
                @Override
                protected Void doInBackground() {
                    String waitingStatus = messages.getString("status.sent_waiting");
                    for (OrderModel order : trackedOrders.values()) {
                        if (waitingStatus.equals(order.getStatus()) || "PROCESSING".equals(order.getStatus())) {
                            try {
                                String remoteStatus = apiClient.fetchOrderStatus(order.getId());
                                if (!remoteStatus.equals(order.getStatus())) {
                                    order.setStatus(remoteStatus);
                                    publish(order);
                                }
                            } catch (Exception ignored) {
                            }
                        }
                    }
                    return null;
                }

                @Override
                protected void process(List<OrderModel> updatedOrders) {
                    for (OrderModel updated : updatedOrders) {
                        updateOrderStatusUI(updated.getId(), updated.getStatus());
                    }
                }
            }.execute();
        });
        timer.start();
    }

    private void updateOrderStatusUI(UUID orderId, String newStatus) {
        SwingUtilities.invokeLater(() -> {
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                if (tableModel.getValueAt(i, 0).equals(orderId)) {
                    tableModel.setValueAt(newStatus, i, 3);
                    break;
                }
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new OrderMainFrame().setVisible(true));
    }
}