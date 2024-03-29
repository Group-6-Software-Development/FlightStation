package simu.model;

import controller.IControllerForM;
import eduni.distributions.Negexp;
import eduni.distributions.Normal;
import simu.entity.VariablesCalculation;
import simu.framework.ArrivalProcess;
import simu.framework.Clock;
import simu.framework.Engine;
import simu.framework.Event;

/**
 * This class is the engine of the simulation. It extends the Engine class from the framework.
 */
public class OwnEngine extends Engine {
    private ArrivalProcess arrivalProcess;
    private ServicePoint[] servicePoints;

    private int arrivalMean = 15;
    private int arrivalVariance = 5;
    private int checkInMean = 10;
    private int checkInVariance = 6;
    private int bagDropMean = 10;
    private int bagDropVariance = 10;
    private int securityMean = 15;
    private int securityVariance = 6;
    private int passportMean = 15;
    private int passportVariance = 10;
    private int ticketInspectionMean = 15;
    private int ticketInspectionVariance = 5;

    private int customerCount = 0;

    /**
     * Constructor of the OwnEngine class
     *
     * @param controller the controller of the simulation
     */
    public OwnEngine(IControllerForM controller) {
        super(controller);
        setupServicePoints();
    }

    /**
     * This method sets up the service points of the simulation
     */
    @Override
    public void setupServicePoints() {
        int[] means = {
                checkInMean, bagDropMean, securityMean, passportMean, ticketInspectionMean
        };

        int[] variances = {
                checkInVariance, bagDropVariance, securityVariance, passportVariance, ticketInspectionVariance
        };

        EventType[] eventTypes = {
                EventType.CHECKIN, EventType.BAGDROP, EventType.SECURITYCHECK, EventType.PASSPORTCHECK, EventType.TICKETINSPECTION
        };

        servicePoints = new ServicePoint[5];

        for (int i = 0; i < 5; i++) {
            servicePoints[i] = new ServicePoint(new Normal(means[i], variances[i]), eventlist, eventTypes[i]);
        }

        arrivalProcess = new ArrivalProcess(new Negexp(arrivalMean, arrivalVariance), eventlist, EventType.ARR1);

        System.out.println("Settings applied");
    }

    /**
     * This method runs the event
     *
     * @param e the event that is run
     */
    @Override
    protected void runEvent(Event e) {
        Customer customer;
        switch ((EventType) e.getType()) {
            case ARR1:
                customer = new Customer();
                servicePoints[0].addToQueue(customer);
                arrivalProcess.generateNext();
                controller.visualizeCustomer();
                break;
            case CHECKIN:
                VariablesCalculation.servicePointC(EventType.CHECKIN);
                customer = servicePoints[0].takeFromQueue();
                customer.setRiStart(Clock.getInstance().getTime());
                VariablesCalculation.servicePointRi(customer.getRiStart(), customer.getRiEnd(), EventType.CHECKIN);
                servicePoints[1].addToQueue(customer);
                break;
            case BAGDROP:
                VariablesCalculation.servicePointC(EventType.BAGDROP);
                customer = servicePoints[1].takeFromQueue();
                customer.setRiStart(Clock.getInstance().getTime());
                VariablesCalculation.servicePointRi(customer.getRiStart(), customer.getRiEnd(), EventType.BAGDROP);
                servicePoints[2].addToQueue(customer);
                break;
            case SECURITYCHECK:
                VariablesCalculation.servicePointC(EventType.SECURITYCHECK);
                customer = servicePoints[2].takeFromQueue();
                customer.setRiStart(Clock.getInstance().getTime());
                VariablesCalculation.servicePointRi(customer.getRiStart(), customer.getRiEnd(), EventType.SECURITYCHECK);
                servicePoints[3].addToQueue(customer);
                break;
            case PASSPORTCHECK:
                VariablesCalculation.servicePointC(EventType.PASSPORTCHECK);
                customer = servicePoints[3].takeFromQueue();
                customer.setRiStart(Clock.getInstance().getTime());
                VariablesCalculation.servicePointRi(customer.getRiStart(), customer.getRiEnd(), EventType.PASSPORTCHECK);
                servicePoints[4].addToQueue(customer);
                break;
            case TICKETINSPECTION:
                VariablesCalculation.servicePointC(EventType.TICKETINSPECTION);
                customer = servicePoints[4].takeFromQueue();
                customer.setRiStart(Clock.getInstance().getTime());
                VariablesCalculation.servicePointRi(customer.getRiStart(), customer.getRiEnd(), EventType.TICKETINSPECTION);
                customer.setDepartureTime(Clock.getInstance().getTime());
                customer.report();
                controller.visualizeCustomerLeaves();
                customerCount++;
                break;
        }
        VariablesCalculation.setSimulationTotalTime(Clock.getInstance().getTime()); // set the total simulation time
    }

    /**
     * This method initializes the simulation
     */
    @Override
    protected void init() {
        arrivalProcess.generateNext();
    }

    /**
     * This method tries to start the service of the service points
     */
    @Override
    protected void tryCEvents() {
        for (ServicePoint servicePoint : servicePoints) {
            if (!servicePoint.isBusy() && servicePoint.hasQueue()) {
                servicePoint.startService();
            }
        }
    }

    /**
     * This method prints the results of the simulation
     */
    @Override
    protected void results() {
        System.out.println("Simulation end at " + Clock.getInstance().getTime());
        System.out.println("Total amount of customers that passed into the plane: " + customerCount);
        controller.showEndTime(Clock.getInstance().getTime());
    }

    /**
     * This method returns the amount of customers that passed into the plane
     * @return the amount of customers that passed into the plane
     */
    @Override
    public int getCustomerCount() {
        return customerCount;
    }

    /**
     * This method sets the settings of the simulation
     * @param values the values of the settings
     */
    @Override
    public void setSettings(int[] values) {
        this.arrivalMean = values[0];
        this.arrivalVariance = values[1];
        this.checkInMean = values[2];
        this.checkInVariance = values[3];
        this.bagDropMean = values[4];
        this.bagDropVariance = values[5];
        this.securityMean = values[6];
        this.securityVariance = values[7];
        this.passportMean = values[8];
        this.passportVariance = values[9];
        this.ticketInspectionMean = values[10];
        this.ticketInspectionVariance = values[11];

        for (int value : values) {
            System.out.println(value);
        }

        setupServicePoints();
    }
}
