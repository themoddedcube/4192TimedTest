// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */

  private WPI_TalonFX leftMaster = new WPI_TalonFX(14);
  private WPI_TalonFX rightMaster = new WPI_TalonFX(3);
  private WPI_TalonFX leftSlave = new WPI_TalonFX(12);
  private WPI_TalonFX rightSlave = new WPI_TalonFX(1);

  private WPI_TalonFX lf = new WPI_TalonFX(2);
  private WPI_TalonFX rf = new WPI_TalonFX(0);
  private WPI_TalonFX lb = new WPI_TalonFX(13);
  private WPI_TalonFX rb = new WPI_TalonFX(15);

  private WPI_TalonFX intake = new WPI_TalonFX(0);
  private WPI_TalonFX index = new WPI_TalonFX(0);

  private DifferentialDrive drive = new DifferentialDrive(leftMaster, rightMaster);
  private Joystick driverJoystick = new Joystick(0);

  private final double kDriveTick2Feet = 1.0 / 4096 * 6 * Math.PI / 12;

  @Override
  public void robotInit() {
    // inverted settings
    leftMaster.setInverted(true);
    rightMaster.setInverted(true);

    // slave setups
    leftSlave.follow(leftMaster);
    rightSlave.follow(rightMaster);

    leftSlave.setInverted(InvertType.FollowMaster);
    rightSlave.setInverted(InvertType.FollowMaster);

    // init encoders
    leftMaster.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor, 0, 10);
    rightMaster.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor, 0, 10);

    leftMaster.setSensorPhase(false);
    rightMaster.setSensorPhase(true);

    // reset encoders to zero
    leftMaster.setSelectedSensorPosition(0, 0, 10);
    rightMaster.setSelectedSensorPosition(0, 0, 10);
  }

  @Override
  public void robotPeriodic() {}

  @Override
  public void autonomousInit() {
    enableMotors();
    // reset encoders to zero
    leftMaster.setSelectedSensorPosition(0, 0, 10);
    rightMaster.setSelectedSensorPosition(0, 0, 10);
  }

  @Override
  public void autonomousPeriodic() {
    double leftPosition = leftMaster.getSelectedSensorPosition() * kDriveTick2Feet;
    double rightPosition = rightMaster.getSelectedSensorPosition() * kDriveTick2Feet;
    double distance = (leftPosition + rightPosition) / 2;

    if (distance < 10) {
      drive.tankDrive(0.6, 0.6);
    } else {
      drive.tankDrive(0, 0);
    }
  }

  @Override
  public void teleopInit() {
    enableMotors();
  }

  @Override
  public void teleopPeriodic() {
     // driving
     double power = -driverJoystick.getRawAxis(1);
     double turn = driverJoystick.getRawAxis(4);
     drive.arcadeDrive(power * 0.6, turn * 0.3);

     double intakePow = driverJoystick.getRawAxis(0)-driverJoystick.getRawAxis(1);
     intake.set(ControlMode.PercentOutput, intakePow);
     index.set(ControlMode.PercentOutput, intakePow);
  }

  @Override
  public void disabledInit() {}

  @Override
  public void disabledPeriodic() {}

  @Override
  public void testInit() {}

  @Override
  public void testPeriodic() {}

  @Override
  public void simulationInit() {}

  @Override
  public void simulationPeriodic() {}

  private void enableMotors() {
    NeutralMode mode = NeutralMode.Brake;
    
    leftMaster.setNeutralMode(mode);
    rightMaster.setNeutralMode(mode);
    leftSlave.setNeutralMode(mode);
    rightSlave.setNeutralMode(mode);

    lf.setNeutralMode(mode);
    rf.setNeutralMode(mode);
    lb.setNeutralMode(mode);
    rb.setNeutralMode(mode);

    intake.setNeutralMode(mode);
    index.setNeutralMode(mode);
  }
}
