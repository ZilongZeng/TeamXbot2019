package competition;

import org.junit.Ignore;

import competition.operator_interface.OperatorInterface;
import competition.subsystems.drive.DriveSubsystem;
import competition.subsystems.pose.PoseSubsystem;
import xbot.common.injection.BaseWPITest;
import xbot.common.injection.UnitTestModule;
import xbot.common.subsystems.drive.BaseDriveSubsystem;
import xbot.common.subsystems.pose.BasePoseSubsystem;

@Ignore
public class BaseCompetitionTest extends BaseWPITest {

    protected OperatorInterface oi;
    protected ElectricalContract2019 contract;
    
    protected class TestModule extends UnitTestModule {
        @Override
        protected void configure() {
            super.configure();

            this.bind(ElectricalContract2019.class).to(ImaginaryRobot2019.class);
            this.bind(BasePoseSubsystem.class).to(PoseSubsystem.class);
            this.bind(BaseDriveSubsystem.class).to(DriveSubsystem.class);
        }
    }

    public BaseCompetitionTest() {
        guiceModule = new TestModule();
    }

    @Override
    public void setUp() {
        super.setUp();
        
        oi = injector.getInstance(OperatorInterface.class);
        contract = injector.getInstance(ElectricalContract2019.class);
    }
}
