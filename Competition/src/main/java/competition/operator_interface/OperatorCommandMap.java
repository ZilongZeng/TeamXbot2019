package competition.operator_interface;

import java.util.ArrayList;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import competition.commandgroups.drivecommandgroups.GoToLoadingStationCommandGroup;
import competition.commandgroups.drivecommandgroups.ScoreOnFrontCargoCommandGroup;
import competition.commandgroups.drivecommandgroups.ScoreOnMidCargoCommandGroup;
import competition.commandgroups.drivecommandgroups.ScoreOnNearCargoCommandGroup;
import competition.subsystems.climber.commands.CalibrateFloorCommand;
import competition.subsystems.climber.commands.DebugMotorClimberCommand;
import competition.subsystems.climber.commands.FrontMotorClimberManualControlCommand;
import competition.subsystems.climber.commands.HoldRearClimberPositionCommand;
import competition.subsystems.climber.commands.HoldRobotLevelCommand;
import competition.subsystems.climber.commands.HoldRobotLevelCommandThatEnds;
import competition.subsystems.climber.commands.RearMotorClimberManualControlCommand;
import competition.subsystems.climber.commands.SetRearLegsToMaximumCommand;
import competition.subsystems.drive.commands.ArcadeDriveWithJoysticksCommand;
import competition.subsystems.drive.commands.CheesyDriveWithJoysticksCommand;
import competition.subsystems.drive.commands.CheesyQuickTurnCommand;
import competition.subsystems.drive.commands.ConfigureDriveSubsystemCommand;
import competition.subsystems.drive.commands.DriveEverywhereCommandGroup;
import competition.subsystems.drive.commands.ForwardRatchetArcadeCommand;
import competition.subsystems.drive.commands.HumanAssistedPurePursuitCommand;
import competition.subsystems.drive.commands.RotateToHeadingCommand;
import competition.subsystems.drive.commands.SetOutreachModeCommand;
import competition.subsystems.drive.commands.TankDriveWithJoysticksCommand;
import competition.subsystems.elevator.ElevatorSubsystem;
import competition.subsystems.elevator.ElevatorSubsystem.HatchLevel;
import competition.subsystems.elevator.commands.ElevatorManualOverrideCommand;
import competition.subsystems.elevator.commands.LowerElevatorCommand;
import competition.subsystems.elevator.commands.RaiseElevatorCommand;
import competition.subsystems.elevator.commands.SetElevatorTickGoalCommand;
import competition.subsystems.elevator.commands.StopElevatorCommand;
import competition.subsystems.gripper.commands.ExtendGripperCommand;
import competition.subsystems.gripper.commands.GrabDiscCommand;
import competition.subsystems.gripper.commands.ReleaseDiscCommand;
import competition.subsystems.gripper.commands.RetractGripperCommand;
import competition.subsystems.gripper.commands.ToggleGrabDiscCommand;
import competition.subsystems.pose.PoseSubsystem;
import competition.subsystems.pose.PoseSubsystem.FieldLandmark;
import competition.subsystems.pose.PoseSubsystem.Side;
import competition.subsystems.pose.SetPoseToFieldLandmarkCommand;
import competition.subsystems.vision.VisionSubsystem;
import competition.subsystems.vision.commands.RotateToVisionTargetCommand;
import edu.wpi.first.wpilibj.command.Command;
import xbot.common.command.SimpleCommandGroup;
import xbot.common.command.SimpleCommandGroup.ExecutionType;
import xbot.common.controls.sensors.AdvancedButton;
import xbot.common.controls.sensors.AnalogHIDButton.AnalogHIDDescription;
import xbot.common.controls.sensors.ChordButton;
import xbot.common.injection.wpi_factories.CommonLibFactory;
import xbot.common.math.FieldPose;
import xbot.common.subsystems.drive.ConfigurablePurePursuitCommand;
import xbot.common.subsystems.drive.PurePursuitCommand.PointLoadingMode;
import xbot.common.subsystems.drive.RabbitPoint;
import xbot.common.subsystems.pose.ResetHeadingAndDistanceCommandGroup;

@Singleton
public class OperatorCommandMap {
        // For mapping operator interface buttons to commands

        // Example for setting up a command to fire when a button is pressed:

        @Inject
        public void setupDriveCommands(OperatorInterface operatorInterface, VisionSubsystem vision,
                        CheesyDriveWithJoysticksCommand cheesyDrive, ArcadeDriveWithJoysticksCommand arcade,
                        TankDriveWithJoysticksCommand tank, RotateToHeadingCommand rotate, ConfigurablePurePursuitCommand pursuit,
                        ResetHeadingAndDistanceCommandGroup resetPose, ConfigurablePurePursuitCommand forward,
                        ConfigurablePurePursuitCommand backward, DriveEverywhereCommandGroup driveEverywhere,
                        ConfigurablePurePursuitCommand goToRocket, ConfigurablePurePursuitCommand goToLoadingStation,
                        ConfigurablePurePursuitCommand goToFrontCargo, ConfigurablePurePursuitCommand goToNearCargo,
                        ConfigurablePurePursuitCommand goToFarLoadingStation, PoseSubsystem poseSubsystem,
                        HumanAssistedPurePursuitCommand goToVisionTarget,
                        ForwardRatchetArcadeCommand ratchetDrive) {
                operatorInterface.driverGamepad.getPovIfAvailable(0).whenPressed(arcade);
                operatorInterface.driverGamepad.getPovIfAvailable(90).whenPressed(arcade);
                operatorInterface.driverGamepad.getPovIfAvailable(180).whenPressed(cheesyDrive);

                driveEverywhere.includeOnSmartDashboard();

                goToRocket.setPointSupplier(
                                () -> poseSubsystem.getPathToLandmark(Side.Left, FieldLandmark.NearRocket, true));
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

                goToNearCargo.setPointSupplier(
                                () -> poseSubsystem.getPathToLandmark(Side.Left, FieldLandmark.NearCargoShip, true));
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
                forward.includeOnSmartDashboard("Move Forward");

                backward.setMode(PointLoadingMode.Relative);
                backward.addPoint(new RabbitPoint(0, -4 * 12, 90));
                backward.includeOnSmartDashboard("Move Backward");

                goToVisionTarget.setMode(PointLoadingMode.Relative);
                goToVisionTarget.setDotProductDrivingEnabled(true);
                goToVisionTarget.setPointSupplier(() -> vision.getVisionTargetRelativePosition());
                operatorInterface.driverGamepad.getifAvailable(7).whileHeld(goToVisionTarget);

                operatorInterface.driverGamepad.getifAvailable(9).whenPressed(ratchetDrive);
        }

        @Inject
        public void setupDriverCommandGroups(OperatorInterface operatorInterface, CommonLibFactory clf,
                        PoseSubsystem pose, ScoreOnMidCargoCommandGroup mid, ScoreOnFrontCargoCommandGroup front,
                        ScoreOnNearCargoCommandGroup near, GoToLoadingStationCommandGroup loading,
                        RotateToHeadingCommand faceForward, RotateToHeadingCommand faceLeft,
                        RotateToHeadingCommand faceRight, RotateToHeadingCommand faceBack,
                        ConfigurablePurePursuitCommand leftLoadingWaypoint,
                        ConfigurablePurePursuitCommand rightLoadingWaypoint, ConfigurablePurePursuitCommand leftLoading,
                        ConfigurablePurePursuitCommand rightLoading, CheesyQuickTurnCommand quickTurn,
                        SetOutreachModeCommand enableOutreach,
                        SetOutreachModeCommand disableOutreach) {
                front.includeOnSmartDashboard("Score on Front Cargo");
                near.includeOnSmartDashboard("Score on Near Cargo");
                mid.includeOnSmartDashboard("Score on Mid Cargo");
                loading.includeOnSmartDashboard("Go to Loading Station");

                faceForward.setHeadingGoal(90, false);
                faceLeft.setHeadingGoal(180, false);
                faceRight.setHeadingGoal(0, false);
                faceBack.setHeadingGoal(-90, false);

                AdvancedButton leftShift = operatorInterface.driverGamepad.getifAvailable(5);
                AdvancedButton driverA = operatorInterface.driverGamepad.getifAvailable(1);
                AdvancedButton driverB = operatorInterface.driverGamepad.getifAvailable(2);
                AdvancedButton driverX = operatorInterface.driverGamepad.getifAvailable(3);
                AdvancedButton driverY = operatorInterface.driverGamepad.getifAvailable(4);

                leftShift.whileHeld(quickTurn);

                ChordButton ab = clf.createChordButton(driverA, driverB);
                ChordButton xy = clf.createChordButton(driverX, driverY);

                enableOutreach.setOutreachModeEnabled(true);
                disableOutreach.setOutreachModeEnabled(false);

                ab.whenPressed(enableOutreach);
                xy.whenPressed(disableOutreach);
        }

        @Inject
        public void setupGripperCommands(OperatorInterface operatorInterface, ReleaseDiscCommand releaseDisc,
                        GrabDiscCommand grabDisc, ExtendGripperCommand extend, RetractGripperCommand retract,
                        ToggleGrabDiscCommand toggleGrab) {
                operatorInterface.operatorGamepad.getifAvailable(2).whenPressed(grabDisc);
                operatorInterface.operatorGamepad.getifAvailable(1).whenPressed(releaseDisc);
                operatorInterface.operatorGamepad.getifAvailable(3).whenPressed(extend);
                operatorInterface.operatorGamepad.getifAvailable(4).whenPressed(retract);
        }

        @Inject
        public void setupElevatorCommands(OperatorInterface operatorInterface, ElevatorSubsystem elevatorSubsystem,
                        RaiseElevatorCommand raiseElevator, LowerElevatorCommand lowerElevator,
                        StopElevatorCommand stopElevator, SetElevatorTickGoalCommand setElevatorLow,
                        SetElevatorTickGoalCommand setElevatorMid, SetElevatorTickGoalCommand setElevatorHigh,
                        ElevatorManualOverrideCommand override) {

                operatorInterface.operatorGamepad.getifAvailable(8).whenPressed(stopElevator);
                operatorInterface.operatorGamepad.getifAvailable(9).toggleWhenPressed(override);

                setElevatorLow.setGoal(elevatorSubsystem.getTickHeightForLevel(HatchLevel.Low));
                setElevatorMid.setGoal(elevatorSubsystem.getTickHeightForLevel(HatchLevel.Medium));
                setElevatorHigh.setGoal(elevatorSubsystem.getTickHeightForLevel(HatchLevel.High));

                operatorInterface.operatorGamepad.getPovIfAvailable(180).whenPressed(setElevatorLow);
                operatorInterface.operatorGamepad.getPovIfAvailable(90).whenPressed(setElevatorMid);
                operatorInterface.operatorGamepad.getPovIfAvailable(270).whenPressed(setElevatorMid);
                operatorInterface.operatorGamepad.getPovIfAvailable(0).whenPressed(setElevatorHigh);
        }

        @Inject
        public void setupPoseCommands(SetPoseToFieldLandmarkCommand setPoseToLeftHabLevelZero,
                        SetPoseToFieldLandmarkCommand setPoseToLeftLoadingStation, OperatorInterface operatorInterface,
                        SetPoseToFieldLandmarkCommand setPositionToLeftLoadingStation,
                        SetPoseToFieldLandmarkCommand setPositionToHab2Left,
                        SetPoseToFieldLandmarkCommand setPositionToHab2Right,
                        SetPoseToFieldLandmarkCommand setPositionToHab1Center) {
                // Start with a smaller set of commands, we can build up from there.
                setPoseToLeftHabLevelZero.setLandmark(Side.Left, FieldLandmark.HabLevelZero);
                setPoseToLeftHabLevelZero.forceHeading(true);
                setPoseToLeftHabLevelZero.includeOnSmartDashboard("Set Pose to Left Level 0");
                setPoseToLeftLoadingStation.setLandmark(Side.Left, FieldLandmark.LoadingStation);
                setPoseToLeftLoadingStation.forceHeading(true);
                setPoseToLeftLoadingStation.includeOnSmartDashboard("Set pose to Left Loading Station");

                setPositionToLeftLoadingStation.setLandmark(Side.Left, FieldLandmark.LoadingStation);
                setPositionToLeftLoadingStation.forceHeading(false);
                // operatorInterface.driverGamepad.getifAvailable(5).whenPressed(setPositionToLeftLoadingStation);

                // Setting where the bot starts after drive teams places them
                setPositionToHab1Center.setLandmark(Side.Left, FieldLandmark.HabLevelOne);
                setPositionToHab2Left.setLandmark(Side.Left, FieldLandmark.HabLevelTwo);
                setPositionToHab2Right.setLandmark(Side.Right, FieldLandmark.HabLevelTwo);

                setPositionToHab1Center.includeOnSmartDashboard("Set Position to Center of HAB Level 1");
                setPositionToHab2Left.includeOnSmartDashboard("Set Position to Left of HAB Level 2");
                setPositionToHab2Right.includeOnSmartDashboard("Set Position to Right of HAB Level 2");
        }

        @Inject
        public void setupVisionCommands(OperatorInterface operatorInterface,
                        RotateToVisionTargetCommand rotateToVisionTargetCommand) {
                rotateToVisionTargetCommand.setContinuousAcquisition(true);
                operatorInterface.driverGamepad.getifAvailable(8).whileHeld(rotateToVisionTargetCommand);
                rotateToVisionTargetCommand.includeOnSmartDashboard("Rotate To Vision Target");
                operatorInterface.driverGamepad.getifAvailable(6).whileHeld(rotateToVisionTargetCommand);
        }

        @Inject
        public void setUpClimberCommands(OperatorInterface operatorInterface,
                        DebugMotorClimberCommand debugMotorClimber,
                        Provider<FrontMotorClimberManualControlCommand> frontManualProvider,
                        RearMotorClimberManualControlCommand rearManual,
                        HoldRobotLevelCommand rearLevel,
                        HoldRobotLevelCommandThatEnds rearLevelThatEnds,
                        HoldRearClimberPositionCommand rearHoldPosition, CalibrateFloorCommand calibrateFloor,
                        SetRearLegsToMaximumCommand rearToMax) {

                var fullManual = new ArrayList<Command>();
                fullManual.add(frontManualProvider.get());
                fullManual.add(rearManual);
                SimpleCommandGroup fullManualGroup = new SimpleCommandGroup("FullManualClimb", fullManual,
                                ExecutionType.Parallel);

                var partialClimb = new ArrayList<Command>();
                var fullClimb = new ArrayList<Command>();
                var climbRear = new ArrayList<Command>();
                climbRear.add(rearLevelThatEnds);
                climbRear.add(rearToMax);
                climbRear.add(rearHoldPosition);
                SimpleCommandGroup climbRearGroup = new SimpleCommandGroup("ClimbRearGroup", climbRear,
                                ExecutionType.Serial);
                fullClimb.add(climbRearGroup);
                fullClimb.add(frontManualProvider.get());
                SimpleCommandGroup fullAutomaticClimbGroup = new SimpleCommandGroup("FullAutomaticClimbGroup",
                                fullClimb, ExecutionType.Parallel);
                partialClimb.add(frontManualProvider.get());
                partialClimb.add(rearLevel);
                SimpleCommandGroup partialClimbGroup = new SimpleCommandGroup("PartialClimbGroup", partialClimb,
                                ExecutionType.Parallel);

                operatorInterface.operatorGamepad.getifAvailable(5).whenPressed(fullManualGroup);
                operatorInterface.operatorGamepad.getifAvailable(6).whenPressed(partialClimbGroup);

                AnalogHIDDescription leftTrigger = new AnalogHIDDescription(2, .25, 1.01);
                operatorInterface.operatorGamepad.addAnalogButton(leftTrigger);
                operatorInterface.operatorGamepad.getAnalogIfAvailable(leftTrigger)
                                .whenPressed(fullAutomaticClimbGroup);

                AnalogHIDDescription rightTrigger = new AnalogHIDDescription(3, .25, 1.01);
                operatorInterface.operatorGamepad.addAnalogButton(rightTrigger);
                operatorInterface.operatorGamepad.getAnalogIfAvailable(rightTrigger).whenPressed(rearHoldPosition);

                calibrateFloor.includeOnSmartDashboard("CalibrateFloorCommand");
        }

        @Inject
        public void setupConfigureDriveSubsystemCommands(ConfigureDriveSubsystemCommand setDriveTalonParameters) {
                setDriveTalonParameters.includeOnSmartDashboard("Set Drive Talon Parameters");
        }
}
