package nextstep.subway.unit;

import static com.navercorp.fixturemonkey.api.experimental.JavaGetterMethodPropertySelector.javaGetter;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import nextstep.subway.path.api.response.Path;
import nextstep.subway.path.domain.PathFinder;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.Station;
import nextstep.subway.utils.FixtureUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;

import java.util.List;

public class PathFinderTest {

    static Station 강남역;
    static Station 역삼역;
    static Station 선릉역;
    static Station 선정릉역;
    static Station 강남구청역;
    static Station 학동역;
    static Station 논현역;
    static Station 런던역;
    static Station 파리역;
    static List<Section> 구간들;
    static PathFinder pathFinder;

    @BeforeEach
    public void setUp() {
        /**
         *                         3             1
         *                 논현 --------- 학동 --------- 강남구청
         *                  |                          |
         *                  |                          |   3
         *              4   |                         선정릉
         *                  |                          |
         *                  |                          |   1
         *                 강남 --------- 역삼 --------- 선릉
         *                         1            2
         *
         *                 런던 --------- 파리
         *                       10000
         */


        강남역 = 역_생성("강남역");
        역삼역 = 역_생성("역삼역");
        선릉역 = 역_생성("선릉역");
        선정릉역 = 역_생성("선정릉역");
        강남구청역 = 역_생성("강남구청역");
        학동역 = 역_생성("학동역");
        논현역 = 역_생성("논현역");
        런던역 = 역_생성("런던역");
        파리역 = 역_생성("파리역");

        구간들 = List.of(
                구간_생성(강남역.getId(), 역삼역.getId(), 1),
                구간_생성(역삼역.getId(), 선릉역.getId(), 2),
                구간_생성(선릉역.getId(), 선정릉역.getId(),  1),
                구간_생성(선정릉역.getId(), 강남구청역.getId(), 3),
                구간_생성(강남구청역.getId(), 학동역.getId(), 1),
                구간_생성(학동역.getId(), 논현역.getId(), 3),
                구간_생성(논현역.getId(), 강남역.getId(), 4),
                구간_생성(파리역.getId(), 런던역.getId(), 10000)
        );

        pathFinder = new PathFinder(구간들);
    }


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


    @Test
    void 최단_경로_조회에_성공한다() {
        // when
        Path result = pathFinder.findPath(강남역.getId(), 선정릉역.getId());

        // then
        assertThat(result.getDistance()).isEqualTo(4);
        assertThat(result.getStationIds()).containsAnyOf(
                강남역.getId(),
                역삼역.getId(),
                선릉역.getId(),
                선정릉역.getId()
        );

    }


    @Test
    void 최단_경로_조회에_성공한다2() {
        // when
        Path result = pathFinder.findPath(런던역.getId(), 파리역.getId());

        // then
        assertThat(result.getDistance()).isEqualTo(10000);
        assertThat(result.getStationIds()).containsAnyOf(
                런던역.getId(),
                파리역.getId()
        );

    }

    @Test
    void 출발역과_종착역이_연결되어_있지_않으면_예외가_발생한다() {
        // when && then
        assertThatThrownBy(() -> pathFinder.findPath(파리역.getId(), 강남역.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("출발역과 종착역이 연결되어 있지 않습니다.");
    }

    @Test
    void 출발역과_종착역이_같으면_예외가_발생한다() {
        // when && then
        assertThatThrownBy(() -> pathFinder.findPath(강남역.getId(), 강남역.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("출발역과 종착역이 같습니다.");
    }

    private static Station 역_생성(String name) {
        return FixtureUtil.getBuilder(Station.class)
                .set(javaGetter(Station::getName), name)
                .sample();
    }

    private static Section 구간_생성(Long upStationId, Long downStationId, int distance) {
        return FixtureUtil.getBuilder(Section.class)
                .set(javaGetter(Section::getUpStationId), upStationId)
                .set(javaGetter(Section::getDownStationId), downStationId)
                .set(javaGetter(Section::getDistance), distance)
                .sample();
    }
}
