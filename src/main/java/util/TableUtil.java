package util;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.lang.reflect.Field;
import java.util.List;

/**
 * Utility class to populate a {@link JTable} with data from a list of objects.
 * Automatically reflects object fields as columns and values as rows.
 * Supports excluding certain fields and handling nested objects.
 */
public class TableUtil {
    /**
     * Populates a JTable with the provided list of objects.
     * Fields like "addressId" can be excluded from the table.
     * If an object contains an "Address" type, it uses its toString() representation.
     *
     * @param <T> the type of objects in the list
     * @param table the {@link JTable} to populate
     * @param data a list of objects to display in the table
     */
    public static <T> void populateTable(JTable table, List<T> data) {
        if (data == null || data.isEmpty()) {
            table.setModel(new DefaultTableModel()); // Set empty model
            return;
        }

        Class<?> clazz = data.get(0).getClass();
        Field[] allFields = clazz.getDeclaredFields();

        List<String> excludedFields = List.of("addressId");

        List<Field> fields = new java.util.ArrayList<>();
        for (Field field : allFields) {
            if (!excludedFields.contains(field.getName())) {
                fields.add(field);
            }
        }

        String[] columnNames = new String[fields.size()];
        for (int i = 0; i < fields.size(); i++) {
            columnNames[i] = fields.get(i).getName();
        }

        Object[][] rowData = new Object[data.size()][fields.size()];
        for (int i = 0; i < data.size(); i++) {
            T item = data.get(i);
            for (int j = 0; j < fields.size(); j++) {
                try {
                    fields.get(j).setAccessible(true);
                    Object value = fields.get(j).get(item);

                    if (value != null && value.getClass().getSimpleName().equals("Address")) {
                        value = value.toString(); // Presupune că Address are override la toString()
                    }

                    rowData[i][j] = value;
                } catch (IllegalAccessException e) {
                    rowData[i][j] = null;
                }
            }
        }

        DefaultTableModel model = new DefaultTableModel(rowData, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table.setModel(model);
        table.revalidate();
        table.repaint();
    }
}
