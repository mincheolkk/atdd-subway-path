package nextstep.subway.path.api.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;


@Getter
public class Path {

    private List<Long> stationIds;
    private int distance;

    @Builder
    public Path(List<Long> stationIds, int distance) {
        this.stationIds = stationIds;
        this.distance = distance;
    }

    public static Path of(List<Long> stationIds, int distance) {
        return Path.builder()
                .stationIds(stationIds)
                .distance(distance)
                .build();
    }
}
