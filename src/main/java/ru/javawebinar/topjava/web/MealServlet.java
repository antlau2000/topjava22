package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.meal.MealRestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.stream.Collectors;

public class MealServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(MealServlet.class);

    private MealRestController controller;
    private ConfigurableApplicationContext appCtx;

    LocalDate startDate = LocalDate.MIN;
    LocalDate endDate = LocalDate.MAX;
    LocalTime startTime = LocalTime.MIN;
    LocalTime endTime = LocalTime.MAX;

    @Override
    public void init() {
        appCtx = new ClassPathXmlApplicationContext("spring/spring-app.xml");
        controller = appCtx.getBean(MealRestController.class);
    }

    @Override
    public void destroy() {
        appCtx.close();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String id = request.getParameter("id");

        if (request.getParameter("save") != null) {
            Meal meal = new Meal(id.isEmpty() ? null : Integer.valueOf(id),
                    LocalDateTime.parse(request.getParameter("dateTime")),
                    request.getParameter("description"),
                    Integer.parseInt(request.getParameter("calories")));

            log.info(meal.isNew() ? "Create {}" : "Update {}", meal);
            if (meal.isNew()) {
                controller.create(SecurityUtil.authUserId(), meal);
            } else {
                controller.update(SecurityUtil.authUserId(), meal);
            }
            response.sendRedirect("meals");
        } else if (request.getParameter("time") != null) {
            String startD = request.getParameter("startDate");
            String endD = request.getParameter("endDate");
            String startT = request.getParameter("startTime");
            String endT = request.getParameter("endTime");
            request.setAttribute("meals",
                    controller.getAll(SecurityUtil.authUserId()).stream()
                            .filter(mealTo ->
                                    DateTimeUtil.isBetweenOpen(mealTo.getDateTime().toLocalDate(),
                                            startD == null || startD.isEmpty() ? startDate : LocalDate.parse(startD),
                                            endD == null || endD.isEmpty() ? endDate : LocalDate.parse(endD)))
                            .filter(mealTo ->
                                    DateTimeUtil.isBetweenHalfOpen(mealTo.getDateTime().toLocalTime(),
                                            startT == null || startT.isEmpty() ? startTime : LocalTime.parse(startT),
                                            endT == null || endT.isEmpty() ? endTime : LocalTime.parse(endT)))
                            .collect(Collectors.toList()));
            request.getRequestDispatcher("/meals.jsp").forward(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        switch (action == null ? "all" : action) {
            case "delete":
                int id = getId(request);
                log.info("Delete {}", id);
                controller.delete(SecurityUtil.authUserId(), id);
                response.sendRedirect("meals");
                break;
            case "create":
            case "update":
                final Meal meal = "create".equals(action) ?
                        new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 1000) :
                        controller.get(SecurityUtil.authUserId(), getId(request));
                request.setAttribute("meal", meal);
                request.getRequestDispatcher("/mealForm.jsp").forward(request, response);
                break;
            case "all":
            default:
                log.info("getAll");
                request.setAttribute("meals",
                        controller.getAll(SecurityUtil.authUserId()));
                request.getRequestDispatcher("/meals.jsp").forward(request, response);
                break;
        }
    }

    private int getId(HttpServletRequest request) {
        String paramId = Objects.requireNonNull(request.getParameter("id"));
        return Integer.parseInt(paramId);
    }
}
