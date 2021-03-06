package competition.subsystems.elevator.commands;

import com.google.inject.Inject;

import competition.operator_interface.OperatorInterface;
import competition.subsystems.elevator.ElevatorSubsystem;
import xbot.common.command.BaseCommand;
import xbot.common.controls.sensors.XTimer;
import xbot.common.injection.wpi_factories.CommonLibFactory;
import xbot.common.logic.HumanVsMachineDecider;
import xbot.common.logic.HumanVsMachineDecider.HumanVsMachineMode;
import xbot.common.math.MathUtils;
import xbot.common.math.PIDFactory;
import xbot.common.math.PIDManager;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.PropertyFactory;

public class ElevatorMaintainerCommand extends BaseCommand {
    final OperatorInterface oi;
    final ElevatorSubsystem elevatorSubsystem;
    final PIDManager positionPid;
    final PIDManager velocityPid;
    final DoubleProperty maximumVelocityProp;
    final HumanVsMachineDecider decider;
    final DoubleProperty maximumThrottlePower;
    final DoubleProperty  currentTickGoal;
    final DoubleProperty currentPower;
    double throttle;

    double previousTickPosition;
    double previousTime;

    @Inject
    public ElevatorMaintainerCommand(OperatorInterface oi, ElevatorSubsystem elevatorSubsystem, PIDFactory pidFactory,
            PropertyFactory propFactory, CommonLibFactory clf) {
        this.oi = oi;
        this.elevatorSubsystem = elevatorSubsystem;
        this.positionPid = pidFactory.createPIDManager(getPrefix() + "PositionPID", .05, 0, 0);

        this.velocityPid = pidFactory.createPIDManager(getPrefix() + "VelocityPID", 0.01, 0, 0);
        this.requires(this.elevatorSubsystem);

        propFactory.setPrefix(this.getPrefix());
        maximumVelocityProp = propFactory.createPersistentProperty("MaximumVelocity", 100);
        maximumThrottlePower = propFactory.createPersistentProperty("MaximumThrottle", 0.5);
        decider = clf.createHumanVsMachineDecider(getPrefix() + "Decider");
        this.currentTickGoal = propFactory.createEphemeralProperty("CurrentTickGoal", 0);

        currentPower = propFactory.createEphemeralProperty("CurrentPower", 0);
    }

    @Override
    public void initialize() {
        log.info("Initializing");
        previousTickPosition = elevatorSubsystem.getElevatorHeightInRawTicks();
        previousTime = XTimer.getFPGATimestamp();
        decider.reset();
    }

    @Override
    public void execute() {
        double currentTickPosition = elevatorSubsystem.getElevatorHeightInRawTicks();
        double currentTime = XTimer.getFPGATimestamp();
        double humanInput = oi.operatorGamepad.getLeftVector().y;
        HumanVsMachineMode deciderMode = decider.getRecommendedMode(humanInput);
        double candidatePower = 0;

        double deltaTicks = currentTickPosition - previousTickPosition;
        double deltaTime = currentTime - previousTime;
        double currentVelocityTicksPerSecond = 0;
        // Quick check for small numbers & zero
        if (Math.abs(deltaTime) > 0.001) {
            currentVelocityTicksPerSecond = deltaTicks / deltaTime;
        }

        switch (deciderMode) {
        case HumanControl:
            candidatePower = humanInput;
            break;
        case Coast:
            candidatePower = 0;
            break;
        case InitializeMachineControl:
            candidatePower = 0;
            elevatorSubsystem.setCurrentPositionAsGoalPosition();
            break;
        case MachineControl:
            // If we are not calibrated yet, then don't try using PID.
            if (!elevatorSubsystem.getIsCalibrated()) {
                candidatePower = 0;
                positionPid.reset();
                break;
            }
            double positionalPower = positionPid.calculate(elevatorSubsystem.getTickGoal(),
                elevatorSubsystem.getElevatorHeightInCalibratedTicks());
        
            candidatePower = positionalPower;
            // First, calculate positional impetus
            /*double positionalPower = positionPid.calculate(elevatorSubsystem.getTickGoal(),
                    elevatorSubsystem.getElevatorHeightInTicks());
            // convert positional impetus into velocity
            candidatePower = positionalPower;

            double velocityGoal = positionalPower * maximumVelocityProp.get();
            double velocityOutput = velocityPid.calculate(velocityGoal, currentVelocityTicksPerSecond);
            // apply velocity output to a throttle, keep it inside some bounds
            throttle += velocityOutput;
            throttle = MathUtils.constrainDouble(throttle, -maximumThrottlePower.get(), maximumThrottlePower.get());
            candidatePower = throttle;*/
            break;
        default:
            candidatePower = 0;
            break;
        }

        elevatorSubsystem.setPower(candidatePower);
        previousTickPosition = currentTickPosition;
        previousTime = currentTime;
    }
}
