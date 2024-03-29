package simu.model;

import simu.framework.IEventType;

/**
 * Enum for the different EventTypes
 */
public enum EventType implements IEventType{
    ARR1, CHECKIN, BAGDROP, SECURITYCHECK, PASSPORTCHECK, TICKETINSPECTION;

    /**
     * Returns the name of the EventType as a String
     * @param type EventType which name is wanted as a string
     * @return String of the EventTypes name
     */
    public static String getEventName(EventType type) {
        return switch (type) {
            case ARR1 -> "ARRIVAL";
            case CHECKIN -> "CHECK-IN";
            case BAGDROP -> "BAG-DROP";
            case SECURITYCHECK -> "SECURITY CHECK";
            case PASSPORTCHECK -> "PASSPORT CHECK";
            case TICKETINSPECTION -> "TICKET INSPECTION";
        };
    }
}
