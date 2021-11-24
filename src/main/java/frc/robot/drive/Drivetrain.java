// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.drive;

import frc.robot.Constants.DriveConstants;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import frc.lib.Pose;
import edu.wpi.first.wpilibj.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.kinematics.SwerveDriveKinematics;
import edu.wpi.first.wpilibj.kinematics.SwerveDriveOdometry;
import edu.wpi.first.wpilibj.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.controller.PIDController;
//import edu.wpi.first.wpilibj.controller.ProfiledPIDController;

// import edu.wpi.first.wpilibj.ADXRS450_Gyro;
// import com.ctre.phoenix.sensors.PigeonIMU;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import com.analog.adis16470.frc.ADIS16470_IMU;

import java.util.Arrays;
import java.util.Collections;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

@SuppressWarnings("PMD.ExcessiveImports")
public class Drivetrain extends SubsystemBase {

  private Preferences prefs = Preferences.getInstance();
  
  // Robot swerve modules
  private final SwerveModule m_frontLeft =
      new SwerveModule(
          DriveConstants.SparkCAN.kFrontLeftDriveMotorPort,
          DriveConstants.SparkCAN.kFrontLeftTurningMotorPort,
          DriveConstants.CANCoder.kFrontLefTurningEncoderPort,
          DriveConstants.CANCoder.kFrontLefTurningEncoderOffset
          );

  private final SwerveModule m_frontRight =
      new SwerveModule(
          DriveConstants.SparkCAN.kFrontRightDriveMotorPort,
          DriveConstants.SparkCAN.kFrontRightTurningMotorPort,
          DriveConstants.CANCoder.kFrontRightTurningEncoderPort,
          DriveConstants.CANCoder.kFrontRightTurningEncoderOffset
          );

  private final SwerveModule m_rearLeft =
      new SwerveModule(
          DriveConstants.SparkCAN.kRearLeftDriveMotorPort,
          DriveConstants.SparkCAN.kRearLeftTurningMotorPort,
          DriveConstants.CANCoder.kRearLeftTurningEncoderPort,
          DriveConstants.CANCoder.kRearLeftTurningEncoderOffset
          );

  private final SwerveModule m_rearRight =
      new SwerveModule(
          DriveConstants.SparkCAN.kRearRightDriveMotorPort,
          DriveConstants.SparkCAN.kRearRightTurningMotorPort,
          DriveConstants.CANCoder.kRearRightTurningEncoderPort,
          DriveConstants.CANCoder.kRearRightTurningEncoderOffset
          );

  private SwerveModule[] modules = {m_frontLeft, m_frontRight, m_rearLeft, m_rearRight};

  // The gyro sensor
  private final Gyro m_gyro =  new ADIS16470_IMU(); // new ADXRS450_Gyro();
  // private final PigeonIMU m_pigeon = new PigeonIMU(DriveConstants.kPigeonPort);

  // Odometry class for tracking robot pose
  SwerveDriveOdometry m_odometry;

  //target pose and controller
  Pose m_targetPose = new Pose();
  PIDController m_thetaController = new PIDController(1.0, 0.0, 0.05);
  //ProfiledPIDController m_thetaController = new ProfiledPIDController(
  //  AutoConstants.kPThetaController, 0, 0, AutoConstants.kThetaControllerConstraints);
    
  /** Creates a new DriveSubsystem. */
  public Drivetrain() {

    // Zero out the gyro.
    m_gyro.calibrate();
    m_gyro.reset();

    m_odometry = new SwerveDriveOdometry(DriveConstants.kDriveKinematics, getHeading());

    for (SwerveModule module: modules) {
      module.resetDistance();
      module.syncTurningEncoders();
    }

    m_thetaController.reset();
    m_thetaController.enableContinuousInput(-Math.PI, Math.PI);
  }

  @Override
  public void periodic() {
    // Update the odometry in the periodic block
    m_odometry.update(
        getHeading(),
        m_frontLeft.getState(),
        m_frontRight.getState(),
        m_rearLeft.getState(),
        m_rearRight.getState());
    
    SmartDashboard.putNumber("Heading", getHeading().getDegrees());

    // SmartDashboard.putNumber("FrontLeft State Raw Reading", modules[0].getTurnEncoder().getPosition());
    // SmartDashboard.putNumber("FrontLeft Adjusted Angle", modules[0].adjustedAngle.getDegrees());
    
    SmartDashboard.putNumber("FrontLeft State Velocity", modules[0].getState().speedMetersPerSecond);
    SmartDashboard.putNumber("FrontLeft State Angle", modules[0].getState().angle.getDegrees());

    SmartDashboard.putNumber("FrontRight Velocity", modules[1].getState().speedMetersPerSecond);
    SmartDashboard.putNumber("FrontRight Angle", modules[1].getState().angle.getDegrees());

    SmartDashboard.putNumber("RearLeft Velocity", modules[2].getState().speedMetersPerSecond);
    SmartDashboard.putNumber("RearLeft Angle", modules[2].getState().angle.getDegrees());

    SmartDashboard.putNumber("RearRight Velocity", modules[3].getState().speedMetersPerSecond);
    SmartDashboard.putNumber("RearRight Angle", modules[3].getState().angle.getDegrees());
    
    SmartDashboard.putNumber("currentAngle", getPose().getRotation().getRadians());
    SmartDashboard.putNumber("targetPoseAngle", m_targetPose.getRotation().getRadians());

    // SmartDashboard.putNumber("FronLeft Turning CANcoder Mag Offset", modules[0].getTurnCANcoder().configGetMagnetOffset());
    // SmartDashboard.putNumber("FronLeft Turning CANcoder Abs Position", modules[0].getTurnCANcoder().getAbsolutePosition());
  }

  /**
   * Rotate the relative orientation of the target pose at a given rate.
   *
   * @param deltaTheta How much to rotate the target orientation per loop.
   */
  public void rotateRelative(Rotation2d deltaTheta) {
    m_targetPose.incrementRotation(deltaTheta);
  }

  /**
   * Set the absolute orientation of the target pose.
   *
   * @param theta The target orientation.
   */
  public void rotateAbsolute(Rotation2d theta) {
    m_targetPose.setRotation(theta);
  }

  /**
   * Get the output of the chassis orientation PID controller.
   *
   */
  public double getThetaDot() {
    double setpoint = m_targetPose.getRotation().getRadians();
    double measurement = getPose().getRotation().getRadians();
    double output = m_thetaController.calculate(measurement, setpoint);
    SmartDashboard.putNumber("PID out", output);
    return output;
  }

  /**
   * Drive the robot with given velocities.
   *
   * @param deltaPose SettablePose object with the desired speeds [m/s and rad/s].
   * @param fieldRelative Whether the translation element of the deltaPose is relative to the field.
   */
  @SuppressWarnings("ParameterName")
  public void drive(Pose deltaPose, boolean fieldRelative) {

    Pose chassisSpeeds = deltaPose;
    if (fieldRelative) {
      chassisSpeeds.incrementRotation(getPose().getRotation());
    }
    
    double x = chassisSpeeds.getX();
    double y = chassisSpeeds.getY();
    double theta = chassisSpeeds.getRotation().getRadians();
    ChassisSpeeds speeds = new ChassisSpeeds(x, y, theta);
    
    SwerveModuleState[] swerveModuleStates =
        DriveConstants.kDriveKinematics.toSwerveModuleStates(speeds);
           
    normalizeDrive(swerveModuleStates, chassisSpeeds);
    setModuleStates(swerveModuleStates);
  }

  /**
   * Method to normalize the swerve module output speeds to within physical limits.
   *
   * @param desiredStates Array of target swerve module states.
   * @param chassisSpeeds SettablePose object with the desired chassis speeds [m/s and rad/s].
   */
  public void normalizeDrive(SwerveModuleState[] desiredStates, Pose chassisSpeeds) {

    double maxVelocity = prefs.getDouble("kMaxSpeedMetersPerSecond",DriveConstants.kMaxSpeedMetersPerSecond);
    double x = chassisSpeeds.getX();
    double y = chassisSpeeds.getY();
    double theta = chassisSpeeds.getRotation().getRadians();

    // Find the how fast the fastest spinning drive motor is spinning                                       
    double realMaxSpeed = 0.0;
    for (SwerveModuleState moduleState : desiredStates) {
      if (Math.abs(moduleState.speedMetersPerSecond) > realMaxSpeed) {
        realMaxSpeed = Math.abs(moduleState.speedMetersPerSecond);
      }
    }
    
    double k = Math.max(Math.sqrt(x*x + y*y), Math.abs(theta));
    if (realMaxSpeed != 0.0) {
        for (SwerveModuleState moduleState : desiredStates) {
          moduleState.speedMetersPerSecond *= (k * maxVelocity / realMaxSpeed);
        }
    }
  }

  /**
   * Sets the swerve ModuleStates.
   *
   * @param desiredStates The desired SwerveModule states.
   */
  public void setModuleStates(SwerveModuleState[] desiredStates) {

    SwerveDriveKinematics.normalizeWheelSpeeds(
        desiredStates, prefs.getDouble("kMaxSpeedMetersPerSecond",DriveConstants.kMaxSpeedMetersPerSecond));

        for (int i = 0; i <= 3; i++) {
          modules[i].setDesiredState(desiredStates[i]);
        }
  }

  public SwerveModuleState[] getModuleStates() {

    SwerveModuleState[] states = new SwerveModuleState[4];

    for (int i = 0; i <= 3; i++) {
      states[i++] = modules[i].getState();
    }

    return states;
  }

  /**
   * Resets the odometry to the specified pose.
   *
   * @param pose The pose to which to set the odometry.
   */
  public void resetOdometry(Pose pose) {
    m_odometry.resetPosition(pose.toPose2d(), getHeading());
  }

  /** Resets the drive encoders to currently read a position of 0. */
  public void resetEncoders() {

    for (SwerveModule module: modules) {
      module.resetEncoders();
    }
  }

  /** Zeroes the heading of the robot. */
  public void zeroHeading() {
    //m_gyro.calibrate();
    //m_gyro.reset();
    //m_pigeon.setYaw(0.0);
    m_odometry.update(
        new Rotation2d(),
        m_frontLeft.getState(),
        m_frontRight.getState(),
        m_rearLeft.getState(),
        m_rearRight.getState());
    m_targetPose.setRotation(new Rotation2d());
  }

  /**
   * Returns the currently-estimated pose of the robot.
   *
   * @return The pose.
   */
  public Pose getPose() {
    return new Pose(getPose2d());
  }

  public Pose2d getPose2d() {
    return m_odometry.getPoseMeters();
  }

  /**
   * Returns the heading of the robot.
   *
   * @return the robot's heading as a Rotation2d
   */
  public Rotation2d getHeading() {
    return m_gyro.getRotation2d();
    // double[] ypr_deg = {0.0, 0.0, 0.0};
    // m_pigeon.getYawPitchRoll(ypr_deg);
    // return new Rotation2d(Math.toRadians(ypr_deg[0]));
  }

  /**
   * Returns the turn rate of the robot.
   *
   * @return The turn rate of the robot, in degrees per second
   */
  public double getTurnRate() {
    return m_gyro.getRate() * (DriveConstants.kGyroReversed ? -1.0 : 1.0);
  }
}
