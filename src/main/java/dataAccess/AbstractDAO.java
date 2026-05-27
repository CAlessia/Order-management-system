package dataAccess;

import Connection.ConnectionFactory;

import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A generic Data Access Object (DAO) that provides common CRUD operations for any type T.
 * Uses Java reflection to dynamically manage objects and SQL interactions.
 *
 * @param <T> The type of the object this DAO manages.
 */
public class AbstractDAO<T> {
    protected static final Logger LOGGER = Logger.getLogger(AbstractDAO.class.getName());

    private final Class<T> type;

    /**
     * Constructs an AbstractDAO and infers the generic type T using reflection.
     */
    @SuppressWarnings("unchecked")
    public AbstractDAO() {
        this.type = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];

    }

    /**
     * Creates a SELECT SQL query string to find entries by a given field.
     *
     * @param field The field name to use in the WHERE clause.
     * @return A SQL SELECT query string.
     */
    private String createSelectQuery(String field) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ");
        sb.append(" * ");
        sb.append(" FROM ");
        sb.append(type.getSimpleName());
        sb.append(" WHERE " + field + " =?");
        return sb.toString();
    }

    /**
     * Retrieves all records of type T from the corresponding database table.
     *
     * @return A list of objects of type T.
     */
    public List<T> findAll() {
        List<T> list = new ArrayList<>();
        String query = "SELECT * FROM " + type.getSimpleName();

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            list = createObjects(resultSet);

        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, type.getName() + "DAO:findAll " + e.getMessage());
        }

        return list;
    }

    /**
     * Retrieves a single object of type T from the database by its ID.
     *
     * @param id The ID of the object to retrieve.
     * @return The object with the specified ID, or null if not found.
     */
    public T findById(int id) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String query = createSelectQuery("id");
        try {
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            resultSet = statement.executeQuery();

            return createObjects(resultSet).get(0);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, type.getName() + "DAO:findById " + e.getMessage());
        } finally {
            ConnectionFactory.close(resultSet);
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
        return null;
    }

private List<T> createObjects(ResultSet resultSet) {
    List<T> list = new ArrayList<T>();
    Constructor[] ctors = type.getDeclaredConstructors();
    Constructor ctor = null;

    for (int i = 0; i < ctors.length; i++) {
        ctor = ctors[i];
        if (ctor.getGenericParameterTypes().length == 0)
            break;
    }
    try {
        while (resultSet.next()) {
            ctor.setAccessible(true);
            T instance = (T) ctor.newInstance();

            for (Field field : type.getDeclaredFields()) {
                String fieldName = field.getName();
                Object value = resultSet.getObject(fieldName);

                System.out.println("Setting field: " + fieldName + " to value: " + value + " (type: " + value.getClass() + ")");

                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(fieldName, type);
                Method method = propertyDescriptor.getWriteMethod();
                method.invoke(instance, value);
            }
            list.add(instance);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return list;
}
    /**
     * Inserts a new object of type T into the database.
     *
     * @param t The object to insert.
     * @return The inserted object with the generated ID set.
     */
    public T insert(T t) {
        StringBuilder fields = new StringBuilder();
        StringBuilder values = new StringBuilder();
        List<Object> params = new ArrayList<>();

        for (Field field : type.getDeclaredFields()) {
            if (field.getName().equalsIgnoreCase("id")) continue;
            field.setAccessible(true);
            try {
                fields.append(field.getName()).append(",");
                values.append("?,");
                params.add(field.get(t));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        String query = "INSERT INTO " + type.getSimpleName() +
                " (" + fields.substring(0, fields.length() - 1) + ") " +
                "VALUES (" + values.substring(0, values.length() - 1) + ")";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                Field idField = type.getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(t, keys.getInt(1));
            }

        } catch (SQLException | NoSuchFieldException | IllegalAccessException e) {
            LOGGER.log(Level.WARNING, "Insert failed: " + e.getMessage());
        }

        return t;
    }

    /**
     * Updates an existing record in the database with new values from object T.
     *
     * @param t The object containing updated values.
     */
    public void update(T t) {
        StringBuilder sb = new StringBuilder();
        List<Object> params = new ArrayList<>();
        Object idValue = null;
        Field idField = null;

        for (Field field : type.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                if (getPrimaryKeyColumn().equalsIgnoreCase(field.getName())) {
                    idField = field;
                    idValue = field.get(t);
                    continue;
                }

                if (field.getType().getPackage() != null && !field.getType().getPackage().getName().startsWith("java")) {
                    Object foreignKeyValue = field.get(t);
                    if (foreignKeyValue != null) {
                        String foreignKeyColumn = field.getName() + "Id";
                        sb.append(foreignKeyColumn).append(" = ?, ");
                        params.add(getForeignKeyValue(foreignKeyValue));
                    }
                } else {
                    sb.append(field.getName()).append(" = ?, ");
                    params.add(field.get(t));
                }
            } catch (IllegalAccessException e) {
                LOGGER.log(Level.WARNING, "Error accessing field: " + field.getName(), e);
            }
        }

        String query = "UPDATE " + type.getSimpleName() + " SET " +
                sb.substring(0, sb.length() - 2) + " WHERE " + getPrimaryKeyColumn() + " = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ps.setObject(params.size() + 1, idValue);
            ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Update failed: " + e.getMessage());
        }

    }

    private Object getForeignKeyValue(Object relatedObject) {
        try {
            Field idField = relatedObject.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            return idField.get(relatedObject);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            LOGGER.log(Level.WARNING, "Error retrieving foreign key value", e);
        }
        return null;
    }

    /**
     * Deletes a record by ID from the database table.
     *
     * @param id The ID of the record to delete.
     */
    public void delete(int id) {
        Connection connection = null;
        PreparedStatement statement = null;
        String query = "DELETE FROM " + type.getSimpleName().toLowerCase() + " WHERE " + getPrimaryKeyColumn() + "=?";

        try {
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Delete failed: " + e.getMessage());
        } finally {
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
    }

    /**
     * Deletes a record by ID using a simplified method.
     *
     * @param id The ID of the record to delete.
     */
    public void deleteById(int id) {
        String query = "DELETE FROM " + type.getSimpleName() + " WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Delete failed: " + e.getMessage());
        }
    }

    private String getPrimaryKeyColumn() {
        for (Field field : type.getDeclaredFields()) {
            if (field.getName().equalsIgnoreCase("id")) {
                return "id";
            }
            if (field.getName().endsWith("Id")) {
                return field.getName();
            }
        }
        throw new RuntimeException("No primary key field found in class: " + type.getSimpleName());
    }
}
