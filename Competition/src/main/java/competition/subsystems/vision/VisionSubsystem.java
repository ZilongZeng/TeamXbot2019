package competition.subsystems.vision;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import xbot.common.command.BaseSubsystem;
import xbot.common.command.PeriodicDataSource;
import xbot.common.controls.sensors.XTimer;
import xbot.common.injection.wpi_factories.CommonLibFactory;
import xbot.common.networking.OffboardCommunicationClient;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.PropertyFactory;
import xbot.common.properties.StringProperty;
import xbot.common.subsystems.drive.RabbitPoint;
import xbot.common.subsystems.drive.RabbitPoint.PointTerminatingType;
import xbot.common.subsystems.drive.RabbitPoint.PointType;

@Singleton
public class VisionSubsystem extends BaseSubsystem implements PeriodicDataSource {

    OffboardCommunicationClient client;
    final StringProperty packetProp;
    private String recentPacket;
    double lastCalledTime;
    double angleToTarget;
    double parsedAngle;
    boolean isCalled;
    boolean numberIsTooBig;
    boolean cannotParseNumber;
    boolean beenTooLong;
    final DoubleProperty differenceBetweenTime;
    final DoubleProperty packetNumberProp;

    @Inject
    public VisionSubsystem(PropertyFactory propMan, CommonLibFactory clf) {
        this.client = clf.createZeromqListener("tcp://10.4.88.12:5801", "");
        propMan.setPrefix(this.getPrefix());
        differenceBetweenTime = propMan.createPersistentProperty("differenceBetweenTime", 1);
        recentPacket = "no packets yet";
        packetProp = propMan.createEphemeralProperty("Packet", recentPacket);        
        packetNumberProp = propMan.createEphemeralProperty("NumPackets", 0);

        client.setNewPacketHandler(packet -> handlePacket(packet));
        client.start();
    }

    public void handlePacket(String packet) {
        recentPacket = packet;
        lastCalledTime = XTimer.getFPGATimestamp();
        packetNumberProp.set(packetNumberProp.get() + 1);
        //VisionData newData;
        try {
            //newData = JSON.std.beanFrom(VisionData.class, recentPacket);
            //parsedAngle = newData.getTargetYaw().intValue();
            parsedAngle = Double.parseDouble(packet);
            cannotParseNumber = false;
        } catch (NumberFormatException e) {
            cannotParseNumber = true;
            parsedAngle = 0.0;
        }

        if (parsedAngle > 180.0 || parsedAngle < -180.0) {
            numberIsTooBig = true;
            parsedAngle = 0.0;
        } else {
            numberIsTooBig = false;
        }
    }

    public boolean isTargetInView() {
        beenTooLong = ((XTimer.getFPGATimestamp() - lastCalledTime) > differenceBetweenTime.get());
        if (beenTooLong || numberIsTooBig || cannotParseNumber) {
            return false;
        }
        return true;
    }

    public double getAngleToTarget() {
        return parsedAngle;
    }

    public List<RabbitPoint> getVisionTargetRelativePosition() {
        var points = new ArrayList<RabbitPoint>();
        // TODO: get data from solve PNP
        RabbitPoint goalPoint = new RabbitPoint(36, 36, 90);
        goalPoint.pointType = PointType.PositionAndHeading;
        goalPoint.terminatingType = PointTerminatingType.Stop;

        points.add(goalPoint);

        return points;
    }

    @Override
    public void updatePeriodicData() {
        if (recentPacket != null) {
            packetProp.set(recentPacket);
        }
    }
}