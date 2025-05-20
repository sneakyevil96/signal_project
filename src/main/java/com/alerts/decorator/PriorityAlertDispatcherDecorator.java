package com.alerts.decorator;
import com.alerts.Alert;
import com.alerts.AlertDispatcher;
/**
 * Adds priority handling to alert dispatching.
 */
public class PriorityAlertDispatcherDecorator implements AlertDispatcher {
    private final AlertDispatcher delegate;
    private final String priorityTag;

    public PriorityAlertDispatcherDecorator(AlertDispatcher delegate, String priorityTag) {
        this.delegate    = delegate;
        this.priorityTag = priorityTag;
    }

    /**
     * Executes the dispatch operation.
     * @param alert Alert.
     */
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