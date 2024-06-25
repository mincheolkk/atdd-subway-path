package nextstep.subway.path.api;

import nextstep.subway.path.api.response.Path;
import nextstep.subway.path.api.response.PathResponse;
import nextstep.subway.section.SectionRepository;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationRepository;
import nextstep.subway.station.StationResponse;
import nextstep.subway.utils.FixtureUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

import static com.navercorp.fixturemonkey.api.experimental.JavaGetterMethodPropertySelector.javaGetter;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
class PathServiceTest {

    @Mock
    StationRepository stationRepository;

    @Mock
    SectionRepository sectionRepository;

    @InjectMocks
    PathService pathService;

    static Station 건대입구역;
    static Station 어린이대공원역;
    static Station 역삼역;
    static Station 뉴욕역;
    static List<Section> 구간들;
    static Path 경로;

    @BeforeEach
    public void setUp() {
        건대입구역 = FixtureUtil.getFixture(Station.class);
        어린이대공원역 = FixtureUtil.getFixture(Station.class);
        역삼역 = FixtureUtil.getFixture(Station.class);
        뉴욕역 = FixtureUtil.getFixture(Station.class);

        구간들 = List.of(
            구간_생성(건대입구역.getId(), 어린이대공원역.getId(), 5),
            구간_생성(역삼역.getId(), 건대입구역.getId(), 40)
        );

        경로 = FixtureUtil.getFixture(Path.class);
    }

    @Test
    void 경로에_포함된_역과_총거리를_반환한다() {
        // given
        given(stationRepository.findById(어린이대공원역.getId())).willReturn(Optional.of(어린이대공원역));
        given(stationRepository.findById(건대입구역.getId())).willReturn(Optional.of(건대입구역));
        given(stationRepository.findById(역삼역.getId())).willReturn(Optional.of(역삼역));
        given(sectionRepository.findAll()).willReturn(구간들);

        // when
        PathResponse response = pathService.getPath(어린이대공원역.getId(), 역삼역.getId());

        // then
        assertThat(response.getDistance()).isEqualTo(45);
        assertThat(response.getStations()).containsExactly(
                StationResponse.of(어린이대공원역),
                StationResponse.of(건대입구역),
                StationResponse.of(역삼역)
        );
    }

    @Test
    void 출발역과_종착역이_연결되지_않으면_에러를_반환한다() {
        // given
        given(stationRepository.findById(어린이대공원역.getId())).willReturn(Optional.of(어린이대공원역));
        given(stationRepository.findById(뉴욕역.getId())).willReturn(Optional.of(뉴욕역));
        given(sectionRepository.findAll()).willReturn(구간들);

        // when && then
        assertThatThrownBy(() -> pathService.getPath(어린이대공원역.getId(), 뉴욕역.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("출발역과 종착역이 연결되어 있지 않습니다.");
    }

    @Test
    void 출발역이_없으면_에러를_반환한다() {
        // given
        given(stationRepository.findById(어린이대공원역.getId())).willReturn(Optional.empty());

        // when && then
        assertThatThrownBy(() -> pathService.getPath(어린이대공원역.getId(), 뉴욕역.getId()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("출발역을 찾을 수 없습니다.");
    }

    @Test
    void 종착역이_없으면_에러를_반환한다() {
        // given
        given(stationRepository.findById(어린이대공원역.getId())).willReturn(Optional.of(어린이대공원역));
        given(stationRepository.findById(뉴욕역.getId())).willReturn(Optional.empty());

        // when && then
        assertThatThrownBy(() -> pathService.getPath(어린이대공원역.getId(), 뉴욕역.getId()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("종착역을 찾을 수 없습니다.");
    }

    @Test
    void 출발역과_종착역이_같으면_에러를_반환한다() {
        // given
        given(stationRepository.findById(어린이대공원역.getId())).willReturn(Optional.of(어린이대공원역));

        // when && then
        assertThatThrownBy(() -> pathService.getPath(어린이대공원역.getId(), 어린이대공원역.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("출발역과 종착역이 같습니다.");
    }


    private static Section 구간_생성(Long upStationId, Long downStationId, int distance) {
        return FixtureUtil.getBuilder(Section.class)
                .set(javaGetter(Section::getUpStationId), upStationId)
                .set(javaGetter(Section::getDownStationId), downStationId)
                .set(javaGetter(Section::getDistance), distance)
                .sample();
    }
}