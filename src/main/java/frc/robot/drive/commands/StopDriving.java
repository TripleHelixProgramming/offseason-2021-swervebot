// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.drive.commands;

import frc.robot.drive.Drivetrain;

public class StopDriving extends Drive {

    public StopDriving(Drivetrain subsystem){
        super(subsystem);
    }
 
    @Override
    public double getX() {
        return 0.0;
    }

    @Override
    public double getY() {
        return 0.0;
    }

    @Override
    public double getTheta() {
        return 0.0;
    }
}