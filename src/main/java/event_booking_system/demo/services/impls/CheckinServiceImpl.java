package event_booking_system.demo.services.impls;

import event_booking_system.demo.entities.Checkin;
import event_booking_system.demo.entities.Order;
import event_booking_system.demo.enums.CheckinStatus;
import event_booking_system.demo.exceptions.AppException;
import event_booking_system.demo.repositories.CheckInRepository;
import event_booking_system.demo.services.CheckinService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static event_booking_system.demo.exceptions.CommonErrorCode.CHECKIN_NOT_FOUND;
import static event_booking_system.demo.exceptions.CommonErrorCode.QR_CODE_ALREADY_USED;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CheckinServiceImpl implements CheckinService {

    CheckInRepository checkinRepository;

    @Override
    public Checkin createCheckin(Checkin checkin) {
        if (isQrCodeUsed(checkin.getQrCode(), CheckinStatus.CHECKED_IN)) {
            throw new AppException(QR_CODE_ALREADY_USED, BAD_REQUEST);
        }
        return checkinRepository.save(checkin);
    }

    @Override
    public Checkin updateCheckin(Checkin checkin) {
        Checkin existingCheckin = checkinRepository.findById(checkin.getId())
                .orElseThrow(() -> new AppException(CHECKIN_NOT_FOUND, NOT_FOUND));
        existingCheckin.setOrder(checkin.getOrder());
        existingCheckin.setCheckinTime(checkin.getCheckinTime());
        existingCheckin.setQrCode(checkin.getQrCode());
        return checkinRepository.save(existingCheckin);
    }

    @Override
    public void deleteCheckin(String id) {
        if (!checkinRepository.existsById(id)) {
            throw new AppException(CHECKIN_NOT_FOUND, NOT_FOUND);
        }
        checkinRepository.deleteById(id);
    }

    @Override
    public Optional<Checkin> findCheckinById(String id) {
        return Optional.ofNullable(checkinRepository.findById(id)
                .orElseThrow(() -> new AppException(CHECKIN_NOT_FOUND, NOT_FOUND)));
    }

    @Override
    public List<Checkin> findAllCheckins() {
        return checkinRepository.findAll();
    }

    @Override
    public Optional<Checkin> findCheckinByOrder(Order order) {
        return Optional.ofNullable(checkinRepository.findByOrderId(order.getId())
                .orElseThrow(() -> new AppException(CHECKIN_NOT_FOUND, NOT_FOUND)));
    }

    @Override
    public Optional<Checkin> findCheckinByQrCode(String qrCode) {
        return Optional.ofNullable(checkinRepository.findByQrCode(qrCode)
                .orElseThrow(() -> new AppException(CHECKIN_NOT_FOUND, NOT_FOUND)));
    }

    @Override
    public List<Checkin> findCheckinsByStatus(CheckinStatus status) {
        return checkinRepository.findByStatus(status);
    }

    @Override
    public boolean isQrCodeUsed(String qrCode, CheckinStatus status) {
        return checkinRepository.existsByQrCodeAndStatus(qrCode, status);
    }
}
