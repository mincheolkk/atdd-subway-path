package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import nextstep.subway.path.api.response.Path;
import nextstep.subway.path.domain.PathFinder;
import nextstep.subway.station.Station;
import nextstep.subway.utils.FixtureUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class PathFinderTest {

    @Test
    void PathFinder_클래스를_이용하면_경로에_포함된_역들과_거리를_확인할_수_있다() {
        // given
        Station 잠실역 = FixtureUtil.getFixture(Station.class);
        Station 건대입구역 = FixtureUtil.getFixture(Station.class);
        Station 어린이대공원역 = FixtureUtil.getFixture(Station.class);

        int distance = 15;

        PathFinder pathFinder = mock(PathFinder.class);

        Path path = Path.builder()
                .stationIds(List.of(잠실역.getId(), 건대입구역.getId(), 어린이대공원역.getId()))
                .distance(distance).build();
        BDDMockito.given(pathFinder.findPath(잠실역.getId(), 어린이대공원역.getId())).willReturn(path);

        // when
        Path actual = pathFinder.findPath(잠실역.getId(), 어린이대공원역.getId());

        // then
        assertThat(actual.getStationIds().size()).isEqualTo(3);
        assertThat(actual.getDistance()).isEqualTo(15);
    }

}
