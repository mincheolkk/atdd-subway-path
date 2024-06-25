package nextstep.subway.path.api;

import lombok.RequiredArgsConstructor;
import nextstep.subway.path.api.response.Path;
import nextstep.subway.path.api.response.PathResponse;
import nextstep.subway.path.domain.PathFinder;
import nextstep.subway.section.SectionRepository;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.StationRepository;
import nextstep.subway.station.StationResponse;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PathService {

    private final StationRepository stationRepository;
    private final SectionRepository sectionRepository;

    public PathResponse getPath(Long source, Long target) {

        validateStation(source, target);

        List<Section> sections = sectionRepository.findAll();
        PathFinder pathFinder = new PathFinder(sections);

        Path path = pathFinder.findPath(source, target);

        List<StationResponse> stationResponses = path.getStationIds().stream()
                .map(stationRepository::findById)
                .map(Optional::get)
                .map(StationResponse::of)
                .collect(Collectors.toList());


        return PathResponse.of(stationResponses, path.getDistance());
    }

    private void validateStation(Long source, Long target) {
        stationRepository.findById(source).orElseThrow(
                () -> new EntityNotFoundException("출발역을 찾을 수 없습니다.")
        );

        stationRepository.findById(target).orElseThrow(
                () -> new EntityNotFoundException("종착역을 찾을 수 없습니다.")
        );

        if (source == target) {
            throw new IllegalArgumentException("출발역과 종착역이 같습니다.");
        }
    }
}
