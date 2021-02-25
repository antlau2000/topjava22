package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;
import static ru.javawebinar.topjava.util.MealsUtil.getTos;
import static ru.javawebinar.topjava.util.ValidationUtil.assureIdConsistent;
import static ru.javawebinar.topjava.util.ValidationUtil.checkNew;

@Controller
public class MealRestController {
    private static final Logger log = getLogger(MealRestController.class);

    private final MealService service;

    public MealRestController(MealService service) {
        this.service = service;
    }

    public Meal create(int userId, Meal meal) {
        log.info("create {} for user {}", meal, userId);
        checkNew(meal);
        return service.create(userId, meal);
    }

    public void update(int userId, Meal meal) {
        log.info("update {} for user {}", meal, userId);
        service.update(userId, meal);
    }

    public void delete(int userId, int id) {
        log.info("delete {} for user {}", id, userId);
        service.delete(userId, id);
    }

    public Meal get(int userId, int id) {
        log.info("create {} for user {}", id, userId);
        return service.get(userId, id);
    }

    public List<MealTo> getAll(int userId) {
        log.info("getAll for {}", userId);
        return getTos(service.getAll(userId), SecurityUtil.authUserCaloriesPerDay());
    }
}