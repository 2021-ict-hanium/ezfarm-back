package com.ezfarm.ezfarmback.notification.scheduler;

import com.ezfarm.ezfarmback.alert.domain.AlertRange;
import com.ezfarm.ezfarmback.alert.domain.AlertRangeRepository;
import com.ezfarm.ezfarmback.facility.domain.hour.Facility;
import com.ezfarm.ezfarmback.facility.domain.hour.FacilityRepository;
import com.ezfarm.ezfarmback.notification.domain.Notification;
import com.ezfarm.ezfarmback.notification.domain.NotificationRepository;
import com.ezfarm.ezfarmback.notification.domain.NotificationSupport;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@EnableScheduling
@Transactional
@Component
public class NotificationScheduler {

  private final NotificationSupport notificationSupport;

  private final FacilityRepository facilityRepository;

  private final AlertRangeRepository alertRangeRepository;

  private final NotificationRepository notificationRepository;

  @Scheduled(cron = "0 0 * * * *", zone = "Asia/Seoul")
  public void checkFacilityScheduler() {
    List<Notification> notifications = new ArrayList<>();

    List<Facility> facilities = facilityRepository.findByMeasureDateStartsWith(
        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH")));

    for (Facility facility : facilities) {
      AlertRange alertRange = alertRangeRepository.findByFarm(facility.getFarm()).orElse(null);
      if (alertRange != null) {
        checkFacility(notifications, facility, alertRange);
        notificationRepository.saveAll(notifications);
        notifications.forEach(notificationSupport::sendNotification);
      }
    }
  }

  private void checkFacility(List<Notification> notifications, Facility facility,
      AlertRange alertRange) {
    //??????
    if (facility.getTmp() > alertRange.getTmpMax()) {
      notifications.add(Notification.builder()
          .farm(facility.getFarm())
          .user(facility.getFarm().getUser())
          .content(facility.getFarm().getName() + "??? ????????? ??????????????? ????????????.")
          .build()
      );
    } else if (facility.getTmp() < alertRange.getTmpMin()) {
      notifications.add(Notification.builder()
          .farm(facility.getFarm())
          .user(facility.getFarm().getUser())
          .content(facility.getFarm().getName() + "??? ????????? ??????????????? ????????????.")
          .build()
      );
    }

    //??????
    if (facility.getHumidity() > alertRange.getHumidityMax()) {
      notifications.add(Notification.builder()
          .farm(facility.getFarm())
          .user(facility.getFarm().getUser())
          .content(facility.getFarm().getName() + "??? ????????? ??????????????? ????????????.")
          .build()
      );
    } else if (facility.getHumidity() < alertRange.getHumidityMin()) {
      notifications.add(Notification.builder()
          .farm(facility.getFarm())
          .user(facility.getFarm().getUser())
          .content(facility.getFarm().getName() + "??? ????????? ??????????????? ????????????.")
          .build()
      );
    }

    //??????
    if (facility.getIlluminance() > alertRange.getImnMax()) {
      notifications.add(Notification.builder()
          .farm(facility.getFarm())
          .user(facility.getFarm().getUser())
          .content(facility.getFarm().getName() + "??? ????????? ??????????????? ????????????.")
          .build()
      );
    } else if (facility.getTmp() < alertRange.getImnMin()) {
      notifications.add(Notification.builder()
          .farm(facility.getFarm())
          .user(facility.getFarm().getUser())
          .content(facility.getFarm().getName() + "??? ????????? ??????????????? ????????????.")
          .build()
      );
    }

    //Co2
    if (facility.getCo2() > alertRange.getCo2Max()) {
      notifications.add(Notification.builder()
          .farm(facility.getFarm())
          .user(facility.getFarm().getUser())
          .content(facility.getFarm().getName() + "??? Co2??? ??????????????? ????????????.")
          .build()
      );
    } else if (facility.getCo2() < alertRange.getCo2Min()) {
      notifications.add(Notification.builder()
          .farm(facility.getFarm())
          .user(facility.getFarm().getUser())
          .content(facility.getFarm().getName() + "??? Co2??? ??????????????? ????????????.")
          .build()
      );
    }

    //ph
    if (facility.getPh() > alertRange.getPhMax()) {
      notifications.add(Notification.builder()
          .farm(facility.getFarm())
          .user(facility.getFarm().getUser())
          .content(facility.getFarm().getName() + "??? ph??? ??????????????? ????????????.")
          .build()
      );
    } else if (facility.getPh() < alertRange.getPhMin()) {
      notifications.add(Notification.builder()
          .farm(facility.getFarm())
          .user(facility.getFarm().getUser())
          .content(facility.getFarm().getName() + "??? ph??? ??????????????? ????????????.")
          .build()
      );
    }

    //mos
    if (facility.getMos() > alertRange.getMosMax()) {
      notifications.add(Notification.builder()
          .farm(facility.getFarm())
          .user(facility.getFarm().getUser())
          .content(facility.getFarm().getName() + "??? ?????? ????????? ??????????????? ????????????.")
          .build()
      );
    } else if (facility.getMos() < alertRange.getMosMin()) {
      notifications.add(Notification.builder()
          .farm(facility.getFarm())
          .user(facility.getFarm().getUser())
          .content(facility.getFarm().getName() + "??? ?????? ????????? ??????????????? ????????????.")
          .build()
      );
    }
  }
}
