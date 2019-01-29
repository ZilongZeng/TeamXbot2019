package competition;

import competition.subsystems.drive.DriveSubsystem;
import competition.subsystems.pose.PoseSubsystem;
import xbot.common.injection.RobotModule;
import xbot.common.networking.OffboardCommunicationClient;
import xbot.common.networking.ZeromqListener;
import xbot.common.subsystems.drive.BaseDriveSubsystem;
import xbot.common.subsystems.pose.BasePoseSubsystem;

public class CompetitionModule extends RobotModule {

    RobotPlatform platform;

    public enum RobotPlatform {
        Competition2018,
        Competition2019,
        Practice2019
    }
    
    public CompetitionModule(RobotPlatform platform) {
        this.platform = platform;
    }
    
    @Override
    protected void configure() {
        super.configure();
        this.bind(BasePoseSubsystem.class).to(PoseSubsystem.class);
        this.bind(BaseDriveSubsystem.class).to(DriveSubsystem.class);
        this.bind(OffboardCommunicationClient.class).to(ZeromqListener.class);
        switch (platform) {
            case Competition2018:
                this.bind(ElectricalContract2019.class).to(Competition2018Contract.class);
                break;
            default:
                break;
        }
    }
}
