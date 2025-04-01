package event_booking_system.demo.services;

import event_booking_system.demo.entities.Checkin;
import event_booking_system.demo.entities.Order;
import event_booking_system.demo.enums.CheckinStatus;

import java.util.List;
import java.util.Optional;

public interface CheckinService {

    Checkin createCheckin(Checkin checkin);

    Checkin updateCheckin(Checkin checkin);

    void deleteCheckin(String id);

    Optional<Checkin> findCheckinById(String id);

    List<Checkin> findAllCheckins();

    Optional<Checkin> findCheckinByOrder(Order order);

    Optional<Checkin> findCheckinByQrCode(String qrCode);

    List<Checkin> findCheckinsByStatus(CheckinStatus status);

    boolean isQrCodeUsed(String qrCode, CheckinStatus status);
}