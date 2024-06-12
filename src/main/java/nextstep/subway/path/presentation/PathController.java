package nextstep.subway.path.presentation;

import nextstep.subway.path.api.PathService;
import nextstep.subway.path.api.response.PathResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class PathController {

    private final PathService pathService;

    public PathController(PathService pathService) {
        this.pathService = pathService;
    }

    @GetMapping("/paths")
    public ResponseEntity<PathResponse> findPath(@RequestParam("source") int source, @RequestParam("target") int target) {

        PathResponse pathResponse = new PathResponse();

        return ResponseEntity.ok().body(pathResponse);
    }
}