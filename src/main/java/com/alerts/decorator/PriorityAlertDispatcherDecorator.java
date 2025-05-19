package com.alerts.decorator;
import com.alerts.Alert;
import com.alerts.AlertDispatcher;
//Wraps an AlertDispatcher, prefixing each alert’s condition with a priority tag
public class PriorityAlertDispatcherDecorator implements AlertDispatcher {
    private final AlertDispatcher delegate;
    private final String priorityTag;

    public PriorityAlertDispatcherDecorator(AlertDispatcher delegate, String priorityTag) {
        this.delegate    = delegate;
        this.priorityTag = priorityTag;
    }

    @Override
    public void dispatch(Alert alert) {
        // Create a new Alert with the same patientId & timestamp but tagged condition
        Alert tagged = new Alert(
                alert.getPatientId(),
                "[" + priorityTag + "] " + alert.getCondition(),
                alert.getTimestamp()
        );
        delegate.dispatch(tagged);
    }
}
