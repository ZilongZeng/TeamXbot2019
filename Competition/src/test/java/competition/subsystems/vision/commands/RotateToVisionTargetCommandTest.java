package competition.subsystems.vision.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;

import competition.BaseCompetitionTest;
import competition.subsystems.drive.DriveSubsystem;
import competition.subsystems.vision.VisionSubsystem;

public class RotateToVisionTargetCommandTest extends BaseCompetitionTest {
    RotateToVisionTargetCommand rotateToVisionTargetCommand;
    DriveSubsystem driveSubsystem;
    VisionSubsystem visionSubsystem;

    @Override
    public void setUp() {
        super.setUp();
        rotateToVisionTargetCommand = injector.getInstance(RotateToVisionTargetCommand.class);
        driveSubsystem = injector.getInstance(DriveSubsystem.class);
        visionSubsystem = injector.getInstance(VisionSubsystem.class);
    }

    @Test
    public void testConstructor() {
        RotateToVisionTargetCommand commandTest = injector.getInstance(RotateToVisionTargetCommand.class);
    }

    @Test
    public void testInitialize() {
        rotateToVisionTargetCommand.initialize();
    }

    @Test
    @Ignore
    public void testExecute() {
        assertEquals(0.0, rotateToVisionTargetCommand.rotation, 0.001);
        assertEquals(0.0, visionSubsystem.getAngleToTarget(), 0.001);
        visionSubsystem.handlePacket("{ \"targetYaw\":100 }");
        visionSubsystem.isTargetInView();
        rotateToVisionTargetCommand.execute();
        assertEquals(1, rotateToVisionTargetCommand.rotation, 0.001);
        assertEquals(100.0, visionSubsystem.getAngleToTarget(), 0.001);
    }

}