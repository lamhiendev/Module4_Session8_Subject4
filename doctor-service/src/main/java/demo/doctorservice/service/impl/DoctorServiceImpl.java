package demo.doctorservice.service.impl;

import demo.doctorservice.entity.Doctor;
import demo.doctorservice.exception.DoctorServiceUnavaialbeException;
import demo.doctorservice.repository.DoctorServiceRepository;
import demo.doctorservice.service.DoctorService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {
    private final DoctorServiceRepository doctorServiceRepository;
    @Override
    public List<Doctor> getAllDoctors() {
        return doctorServiceRepository.findAll();
    }

    @Override
    @CircuitBreaker(name = "doctorServiceCB", fallbackMethod = "fallBackDoctor")
    @RateLimiter(name = "searchDoctorLimit" , fallbackMethod = "fallBackDoctor")
    public Doctor getDoctorById(Long id) {
        Doctor newDoctor = doctorServiceRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy bác sĩ với ID " + id));
        return newDoctor;
    }



    public  Doctor fallBackDoctor(Long doctorId, Exception e){
        log.warn("Fallback kích hoạt lí do {}", e.getMessage());
        if (e instanceof RequestNotPermitted) {
            throw new DoctorServiceUnavaialbeException("Hệ thống đang bận do có quá nhiều yêu cầu. Vui lòng thử lại sau vài giây!");
        }
        throw new DoctorServiceUnavaialbeException("Hiện tại không thể kiểm tra thông tin bác sĩ, vui lòng thử lại sau");
    }

}
