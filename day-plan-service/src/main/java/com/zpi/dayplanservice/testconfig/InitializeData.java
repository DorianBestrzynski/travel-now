package com.zpi.dayplanservice.testconfig;

import com.zpi.dayplanservice.attraction.Attraction;
import com.zpi.dayplanservice.attraction.AttractionRepository;
import com.zpi.dayplanservice.day_plan.DayPlan;
import com.zpi.dayplanservice.day_plan.DayPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

@Component
@RequiredArgsConstructor
public class InitializeData {
    @Value("${spring.profiles.active:default}")
    private String profile;

    private final DayPlanRepository dayPlanRepository;

    private final AttractionRepository attractionRepository;

    @PostConstruct
    public void addDayPlans() {
        if (!profile.equals("test"))
            return;

        var dayplans = List.of(
                new DayPlan(1L, LocalDate.now(), "Pałac kultury", 1),
                new DayPlan(1L, LocalDate.now().plusDays(1), "Stadion narodowy", 2),
                new DayPlan(1L, LocalDate.now().plusDays(2), "Skierniewice", 3),
                new DayPlan(5L, LocalDate.now(), "Kabanos arena", 4),
                new DayPlan(5L, LocalDate.now().plusDays(1), "Wyspa słodowa", 5)
        );

        dayPlanRepository.saveAll(dayplans);

        addAttractions();
    }

    @Transactional
    public void addAttractions() {
        if (!profile.equals("test"))
            return;

        var attractionsFirstDayWarsaw = List.of(
                new Attraction("Restauracja Bubbles Warszawa", "description", "Monday: 12:00 – 11:00 PM", "plac Marszałka Józefa Piłsudskiego 9, 00-078 Warszawa, Poland", "https://maps.google.com/?cid=11450681702385177954", "\"AW30NDw2WZrZOWJ4YVkV1owtDGc2M1TPiXwNDDgDglJJfcyr3QA7Fpq7BvWqp0jv9DdSu4uWvG04XbUsQnksI-oOG3hu_2uT99eHSdoUUXEc3rSLPUlLvWshBY42X5O71x2hPkucMbjC5PafYNUoLJYOUCcJ9iMxHi35Ssy6hQQq6JAEMNp1", 52.2430884, 21.0116695),
                new Attraction("Prime Cut | The best steak in Warsaw", "description", "Twarda 18, 00-105 Warszawa, Poland", "Monday: 12:00 – 11:00 PM",  "https://maps.google.com/?cid=4408076564099335096", "AW30NDw7RwQTX5TcUn0XxZy7DBlvRVezYlXDDySNMVO8vhq6F0xLM9Zoak1kuz0MElBIeZj4X-rHjI78Wsr9fNdK_IBZWp0yCsRDdt_nTOsdOcxnsZCmEL6UHgP47wcIdFMHluwBW3t7SuDbzteX2zJEmwoQIcuNDwIuLc20ws-tP2I5UrPj", 52.2344719, 20.999164)
        );

        var attractionsSecondDayWarsaw = List.of(
                new Attraction("SELAVI Restaurant & Bar", "description", "Monday: 12:00 – 10:00 PM", "plac Stanisława Małachowskiego 2, 00-066 Warszawa, Poland", "https://maps.google.com/?cid=16420009412261422650", "AW30NDwfJnu4l63j1XEy5SW_1SehYWNjab5dMmu5TC76W0-QEylatwI9bI-n101j0Q3kG9oaSVHJP_jgpwJtko-X22r0-Wh-7RBIrWxqhR1FniNbLGfPm560Z9Dp2yEPS_40e-u8Mqhw5JlP9TbvEtRwa7RR4MTms-HvF_MYokC6GWbtTeik", 52.2390517, 21.0128005)
        );

        var attractionsThirdDayWarsaw = List.of(
                new Attraction("Soul Kitchen", "description", "Monday: 12:00 – 10:00 PM", "Nowogrodzka 18A, 00-511 Warszawa, Poland", "https://maps.google.com/?cid=380045710953465347", "AW30NDyUkYFXSyQeBssKdopWunynOoWxXTNMaNbV5Pca5ghRvD5VIi3IRWX4vxG3c6Lt80772O29joH4bDnf2riPlb73F7CQ6nafgb6LVZ4ybkOJS3dBKf6HtA5qaPn3NzJODu7ZO9tBB3BnhWQpatv7awJ6q5gk20eVzzjnSh4YwPxRK3gw", 52.229555, 21.0148867)
        );

        var attractionsFirstDayWroclaw = List.of(
                new Attraction("Wrocław Stadium", "description", "Monday: 12:00 – 10:00 PM", "aleja Śląska 1, 54-118 Wrocław, Poland", "https://maps.google.com/?cid=17352084105785538829", "AW30NDyeogrkNWOil7LYibpiZGigKD02QMbJnJF4rWKb7E8oKztdMZO1_ZEw5ag2awArpiNUQOb762JvAMfB-R5yiRt42SZYaSgQX3p7NXQ19SSVAVYupko_gzQa6RMZ1RKYqLUuHRfZmANkjC-jvebsIbV9ZZfxyRfApCafowd_TFMNBUPJ", 51.1412218, 16.9443929),
                new Attraction("pasaż Niepolda", "description", null, "pasaż Niepolda, 50-043 Wrocław, Poland", "https://maps.google.com/?q=pasa%C5%BC+Niepolda,+50-043+Wroc%C5%82aw,+Poland&ftid=0x470fc20b23e6bdef:0x501d43ecce4cdd1a", null, 51.1099021, 17.0254639)
        );

        var attractionsSecondDayWroclaw = List.of(
                new Attraction("Wyspa Słodowa", "description", null, "Wyspa Słodowa, Wrocław, Poland", "https://maps.google.com/?q=Wyspa+S%C5%82odowa,+Wroc%C5%82aw,+Poland&ftid=0x470fe9d952c0ffeb:0xd069b8f3a284913e", "AW30NDygJ7cZ-C3n-QGa9WqRb87kCjgz8G_UExkCMZmIkY0AlPk4eJSMEoRW8sK3ayGYwblVKKgoEHzUtot5lvU2eH64yLnZWl84NsrgafnMIxDxc9OY8WFJ9hufCfSVmwEUPP8mAlPK5G1Fo69nZrD_MyHFWUtPitZYee0YwTuzCtsV0meR", 51.1161092, 17.0375204)
        );

        attractionRepository.saveAll(attractionsFirstDayWarsaw);
        attractionRepository.saveAll(attractionsSecondDayWarsaw);
        attractionRepository.saveAll(attractionsThirdDayWarsaw);
        attractionRepository.saveAll(attractionsFirstDayWroclaw);
        attractionRepository.saveAll(attractionsSecondDayWroclaw);

        var warsawFirstDay = dayPlanRepository.findById(1L).get();
        warsawFirstDay.setDayAttractions(new HashSet<>(attractionsFirstDayWarsaw));
        attractionsFirstDayWarsaw.parallelStream().forEach(attraction -> attraction.addDays(List.of(warsawFirstDay)));
        dayPlanRepository.save(warsawFirstDay);

        var warsawSecondDay = dayPlanRepository.findById(2L).get();
        warsawSecondDay.setDayAttractions(new HashSet<>(attractionsSecondDayWarsaw));
        attractionsSecondDayWarsaw.parallelStream().forEach(attraction -> attraction.addDays(List.of(warsawSecondDay)));
        dayPlanRepository.save(warsawSecondDay);

        var warsawThirdDay = dayPlanRepository.findById(3L).get();
        warsawThirdDay.setDayAttractions(new HashSet<>(attractionsThirdDayWarsaw));
        attractionsThirdDayWarsaw.parallelStream().forEach(attraction -> attraction.addDays(List.of(warsawThirdDay)));
        dayPlanRepository.save(warsawThirdDay);

        var wroclawFirstDay = dayPlanRepository.findById(4L).get();
        wroclawFirstDay.setDayAttractions(new HashSet<>(attractionsFirstDayWroclaw));
        attractionsFirstDayWroclaw.parallelStream().forEach(attraction -> attraction.addDays(List.of(wroclawFirstDay)));
        dayPlanRepository.save(wroclawFirstDay);
    }
}
