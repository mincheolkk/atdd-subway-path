package nextstep.subway.unit;

import nextstep.subway.path.api.response.PathResponse;
import nextstep.subway.path.domain.PathFinder;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class PathTest {

    @DisplayName("PathFinder 클래스를 이용하면 경로에 포함된 역들과 거리를 확인할 수 있다.")
    @Test
    void test() {
        // given
        Station firstStation = new Station("firstStation");
        Station secondStation = new Station("secondStation");
        Station fourthStation = new Station("fourthStation");

        List<StationResponse> stationResponses = List.of(
                StationResponse.of(firstStation),
                StationResponse.of(secondStation),
                StationResponse.of(fourthStation)
        );
        int distance = 15;

        PathFinder pathFinder = mock(PathFinder.class);

        PathResponse pathResponse = PathResponse.of(stationResponses, distance);
        BDDMockito.given(pathFinder.findPath(firstStation, fourthStation)).willReturn(pathResponse);

        // when
        PathResponse response = pathFinder.findPath(firstStation, fourthStation);

        // then
        assertThat(response.getStations().size()).isEqualTo(3);
        assertThat(response.getDistance()).isEqualTo(15);
    }
}
