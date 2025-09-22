package bajaj.Assesment;

import bajaj.Assesment.service.AssessmentService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AssesmentApplication {
    @Autowired
    private AssessmentService assessmentService;

	public static void main(String[] args) {
		SpringApplication.run(AssesmentApplication.class, args);

	}
    @PostConstruct
    public void runAssessmentFlow() {
        assessmentService.executeAssessment();
    }

}
