package competition.operator_interface;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import competition.subsystems.drive.commands.ArcadeDriveWithJoysticksCommand;
import competition.subsystems.drive.commands.CheesyDriveWithJoysticksCommand;
import competition.subsystems.drive.commands.CheesyQuickTurnCommand;
import competition.subsystems.drive.commands.DriveEverywhereCommandGroup;
import competition.subsystems.drive.commands.HumanAssistedPurePursuitCommand;
import competition.subsystems.drive.commands.RotateToHeadingCommand;
import competition.subsystems.drive.commands.TankDriveWithJoysticksCommand;
import competition.subsystems.elevator.commands.LowerElevatorCommand;
import competition.subsystems.elevator.commands.RaiseElevatorCommand;
import competition.subsystems.elevator.commands.StopElevatorCommand;
import competition.subsystems.gripper.commands.ExtendGripperCommand;
import competition.subsystems.gripper.commands.GrabDiscCommand;
import competition.subsystems.gripper.commands.ReleaseDiscCommand;
import competition.subsystems.gripper.commands.RetractGripperCommand;
import competition.subsystems.pose.PoseSubsystem;
import competition.subsystems.pose.PoseSubsystem.FieldLandmark;
import competition.subsystems.pose.PoseSubsystem.Side;
import competition.subsystems.pose.SetPoseToFieldLandmarkCommand;
import xbot.common.controls.sensors.AnalogHIDButton.AnalogHIDDescription;
import xbot.common.controls.sensors.XJoystick;
import xbot.common.subsystems.drive.ConfigurablePurePursuitCommand;
import xbot.common.subsystems.drive.PurePursuitCommand.PointLoadingMode;
import xbot.common.subsystems.drive.RabbitPoint;
import xbot.common.subsystems.pose.ResetHeadingAndDistanceCommandGroup;

@Singleton
public class OperatorCommandMap {
    // For mapping operator interface buttons to commands

    // Example for setting up a command to fire when a button is pressed:

    @Inject
    public void setupDriveCommands(OperatorInterface operatorInterface, CheesyDriveWithJoysticksCommand cheesyDrive,
            ArcadeDriveWithJoysticksCommand arcade, TankDriveWithJoysticksCommand tank, RotateToHeadingCommand rotate,
            CheesyQuickTurnCommand quickTurn, ConfigurablePurePursuitCommand pursuit,
            ResetHeadingAndDistanceCommandGroup resetPose, ConfigurablePurePursuitCommand forward,
            ConfigurablePurePursuitCommand backward, DriveEverywhereCommandGroup driveEverywhere,
            HumanAssistedPurePursuitCommand goToRocket, HumanAssistedPurePursuitCommand goToLoadingStation,
            HumanAssistedPurePursuitCommand goToFrontCargo, HumanAssistedPurePursuitCommand goToNearCargo,
            HumanAssistedPurePursuitCommand goToFarLoadingStation, PoseSubsystem poseSubsystem) {
        operatorInterface.gamepad.getifAvailable(6).whileHeld(quickTurn);
        operatorInterface.gamepad.getPovIfAvailable(0).whenPressed(arcade);
        operatorInterface.gamepad.getPovIfAvailable(90).whenPressed(arcade);
        operatorInterface.gamepad.getPovIfAvailable(270).whenPressed(arcade);
        operatorInterface.gamepad.getPovIfAvailable(180).whenPressed(cheesyDrive);

        driveEverywhere.includeOnSmartDashboard();

        goToRocket.setPointSupplier(() -> poseSubsystem.getPathToLandmark(Side.Left, FieldLandmark.NearRocket, true));
        goToRocket.setDotProductDrivingEnabled(true);
        goToRocket.includeOnSmartDashboard("Go To Rocket");

        goToLoadingStation.setPointSupplier(
                () -> poseSubsystem.getPathToLandmark(Side.Left, FieldLandmark.LoadingStation, true));
        goToLoadingStation.setDotProductDrivingEnabled(true);
        goToLoadingStation.includeOnSmartDashboard("Go To Loading Station");

        goToFarLoadingStation.setPointSupplier(
                () -> poseSubsystem.getPathToLandmark(Side.Right, FieldLandmark.LoadingStation, true));
        goToFarLoadingStation.setDotProductDrivingEnabled(true);
        goToFarLoadingStation.includeOnSmartDashboard("Go To Far Loading Station");

        goToFrontCargo.setPointSupplier(
                () -> poseSubsystem.getPathToLandmark(Side.Left, FieldLandmark.FrontCargoShip, true));
        goToFrontCargo.setDotProductDrivingEnabled(true);
        goToFrontCargo.includeOnSmartDashboard("Go To Front Cargo");

        goToNearCargo
                .setPointSupplier(() -> poseSubsystem.getPathToLandmark(Side.Left, FieldLandmark.NearCargoShip, true));
        goToNearCargo.setDotProductDrivingEnabled(true);
        goToNearCargo.includeOnSmartDashboard("Go To Near Cargo");
        

        pursuit.setMode(PointLoadingMode.Relative);
        pursuit.addPoint(new RabbitPoint(3 * 12, 3 * 12, 0));
        pursuit.includeOnSmartDashboard("Calibrate PP Box Turn");

        rotate.setHeadingGoal(90, true);
        rotate.includeOnSmartDashboard("Calibrate Rotation 90");
        resetPose.includeOnSmartDashboard("Reset Pose to 0 0 90");

        forward.setMode(PointLoadingMode.Relative);
        forward.addPoint(new RabbitPoint(0, 4 * 12, 90));
        operatorInterface.gamepad.getifAvailable(7).whenPressed(forward);

        backward.setMode(PointLoadingMode.Relative);
        backward.addPoint(new RabbitPoint(0, -4 * 12, 90));
        operatorInterface.gamepad.getifAvailable(8).whenPressed(backward);
    }

    @Inject
    public void setupGripperCommands(OperatorInterface operatorInterface, ReleaseDiscCommand releaseDisc,
            GrabDiscCommand grabDisc, ExtendGripperCommand extend, RetractGripperCommand retract) {
        operatorInterface.gamepad.getifAvailable(1).whenPressed(grabDisc);
        operatorInterface.gamepad.getifAvailable(2).whenPressed(releaseDisc);
        operatorInterface.gamepad.getifAvailable(3).whenPressed(extend);
        operatorInterface.gamepad.getifAvailable(4).whenPressed(retract);
    }

    @Inject
    public void setupElevatorCommands(OperatorInterface operatorInterface, RaiseElevatorCommand raiseElevator,
            LowerElevatorCommand lowerElevator, StopElevatorCommand stopElevator) {
        AnalogHIDDescription triggerRaise = new AnalogHIDDescription(3, .25, 1.01);
        operatorInterface.gamepad.addAnalogButton(triggerRaise);
        operatorInterface.gamepad.getAnalogIfAvailable(triggerRaise).whenPressed(raiseElevator);

        AnalogHIDDescription triggerLower = new AnalogHIDDescription(2, .25, 1.01);
        operatorInterface.gamepad.addAnalogButton(triggerLower);
        operatorInterface.gamepad.getAnalogIfAvailable(triggerLower).whenPressed(lowerElevator);
    }

    @Inject
    public void setupPoseCommands(SetPoseToFieldLandmarkCommand setPoseToLeftHabLevelZero,
            SetPoseToFieldLandmarkCommand setPoseToLeftLoadingStation) {
        // Start with a smaller set of commands, we can build up from there.
        setPoseToLeftHabLevelZero.setLandmark(Side.Left, FieldLandmark.HabLevelZero);
        setPoseToLeftHabLevelZero.forceHeading(true);
        setPoseToLeftHabLevelZero.includeOnSmartDashboard("Set Pose to Left Level 0");
        setPoseToLeftLoadingStation.setLandmark(Side.Left, FieldLandmark.LoadingStation);
        setPoseToLeftLoadingStation.forceHeading(true);
        setPoseToLeftLoadingStation.includeOnSmartDashboard("Set pose to Left Loading Station");
    }
}
