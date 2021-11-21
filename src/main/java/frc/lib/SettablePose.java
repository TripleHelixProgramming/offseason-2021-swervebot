package frc.lib;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj.geometry.Transform2d;

public class SettablePose extends Pose2d {
    
    public SettablePose(Translation2d translation, Rotation2d rotation) {
        super(translation, rotation);
    }

    public void incrementTranslation(Translation2d newTranslation) {
        this.transformBy(new Transform2d(newTranslation, new Rotation2d()));
    }
    
    public void setTranslation(Translation2d newTranslation) {
        this.transformBy(new Transform2d(newTranslation.minus(this.getTranslation()), new Rotation2d()));
    }

    public void incrementRotation(Rotation2d newRotation) {
        this.transformBy(new Transform2d(new Translation2d(), newRotation));
    }

    public void setRotation(Rotation2d newRotation) {
        this.transformBy(new Transform2d(new Translation2d(), newRotation.minus(this.getRotation())));
    }

    public Pose2d getPose() {
        return new Pose2d(this.getTranslation(), this.getRotation());
    }

}
