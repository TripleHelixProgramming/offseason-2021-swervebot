// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.drive.commands;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.drive.Drivetrain;

public class ZeroHeading extends CommandBase {
    // The subsystem the command runs on
    private final Drivetrain drivetrain;

    public ZeroHeading(Drivetrain subsystem){
        drivetrain = subsystem;
        addRequirements(drivetrain);
    }
 
    @Override
    public void initialize() {
    }
            
    @Override
    public boolean runsWhenDisabled() {
        return true;
    }
      
    @Override
    public void execute() {
        SmartDashboard.putString("Zero Heading", "Resetting Heading");
        drivetrain.zeroHeading();
        drivetrain.resetOdometry(new Pose2d(new Translation2d(0,0),new Rotation2d(0)));
    }
      
    @Override
    public boolean isFinished() {
        return true;
    }
}
