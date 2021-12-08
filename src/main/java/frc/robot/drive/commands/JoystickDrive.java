// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.drive.commands;

import frc.robot.Constants.DriveConstants;
import frc.robot.drive.Drivetrain;
import frc.robot.oi.OI;

import frc.lib.Curve;
import frc.lib.LinCurve;

public class JoystickDrive extends Drive {

    double xyJoyScale = DriveConstants.kMaxTranslationalVelocity;
    Curve xyJoyMap = new LinCurve(0.0, xyJoyScale, 0.4);

    double thetaJoyScale = DriveConstants.kMaxRotationalVelocity;
    Curve thetaJoyMap = new LinCurve(0.0, thetaJoyScale, 0.4);

    OI m_OI = OI.getInstance();

    public JoystickDrive(Drivetrain subsystem){
        super(subsystem);
    }
 
    @Override
    public double getX() {
        double xRaw = m_OI.getTranslateX();
        return xyJoyMap.calculateMappedVal(xRaw);
    }

    @Override
    public double getY() {
        double yRaw = m_OI.getTranslateY();
        return xyJoyMap.calculateMappedVal(yRaw);
    }

    @Override
    public double getTheta() {
        double thetaRaw = m_OI.getRotation();
        return thetaJoyMap.calculateMappedVal(thetaRaw);
    }

    @Override
    public boolean getFieldRelative() {
        return true;
    }
}