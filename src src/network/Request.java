package network;

import java.io.Serializable;

public class Request implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum RequestType {
        LOGIN,
        REGISTER,
        ADD_DONATION,
        GET_DONATIONS,
        APPROVE_DONATION,
        REJECT_DONATION,
        ADD_NEED,
        GET_NEEDS,
        GET_USERS_BY_ROLE,
        GET_COMPANIES,
        GET_VOLUNTEERS,
        GET_VOLUNTEER_TASKS,
        ASSIGN_TASK,
        APPROVE_TASK_ASSIGNMENT,
        REJECT_TASK_ASSIGNMENT,
        GET_NOTIFICATIONS,
        MARK_NOTIFICATION_READ
    }

    private RequestType type;
    private Object data;
    private int userId; // ID of the user making the request

    public Request(RequestType type, Object data, int userId) {
        this.type = type;
        this.data = data;
        this.userId = userId;
    }

    // Getters and Setters
    public RequestType getType() {
        return type;
    }

    public void setType(RequestType type) {
        this.type = type;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Request{" +
               "type=" + type +
               ", data=" + data +
               ", userId=" + userId +
               '}';
    }
}
