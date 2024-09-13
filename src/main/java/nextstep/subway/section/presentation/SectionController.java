package nextstep.subway.section.presentation;

import lombok.RequiredArgsConstructor;
import nextstep.subway.path.api.PathService;
import nextstep.subway.section.api.SectionService;
import nextstep.subway.section.api.response.SectionResponse;
import nextstep.subway.section.presentation.request.SectionCreateRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RequiredArgsConstructor
@RestController
public class SectionController {

    private final SectionService sectionService;
    private final PathService pathService;

    @PostMapping("/lines/{lineId}/sections")
    public ResponseEntity<SectionResponse> createSection(@PathVariable Long lineId, @RequestBody SectionCreateRequest request) {
        SectionResponse sectionResponse = sectionService.create(lineId, request);
        pathService.cacheData();
        return ResponseEntity.created(URI.create("/lines/" + lineId + "/sections/" + sectionResponse.getSectionId())).body(sectionResponse);
    }

    @DeleteMapping("/lines/{lineId}/sections")
    public ResponseEntity<Void> deleteSection(@PathVariable Long lineId, @RequestParam Long stationId) {
        sectionService.delete(lineId, stationId);
        pathService.cacheData();
        return ResponseEntity.noContent().build();
    }
}
