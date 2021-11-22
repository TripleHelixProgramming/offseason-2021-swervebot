// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.drive.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import frc.robot.drive.Drivetrain;
import frc.lib.Pose;

public abstract class Drive extends CommandBase {

    private Translation2d xyDot;
    private Rotation2d thetaDot;
    private boolean fieldRelative;
    private Pose deltaPose;

    // The subsystem the command runs on
    public final Drivetrain drivetrain;

    public Drive(Drivetrain subsystem){
        drivetrain = subsystem;
        addRequirements(drivetrain);
    }
 
    @Override
    public void initialize() {
    }
            
    @Override
    public void execute() {
        xyDot = new Translation2d(getX(), getY());
        thetaDot = new Rotation2d(getTheta());
        deltaPose = new Pose(xyDot, thetaDot);
        fieldRelative = getFieldRelative();

        drivetrain.drive(deltaPose, fieldRelative);
    }

    abstract public double getX();
    abstract public double getY();
    abstract public double getTheta();

    public boolean getFieldRelative() {
        return false;
    };
}
