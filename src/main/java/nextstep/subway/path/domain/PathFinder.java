package nextstep.subway.path.domain;

import lombok.Getter;
import nextstep.subway.path.api.response.Path;
import nextstep.subway.section.domain.Section;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

@Getter
public class PathFinder {

    private final WeightedMultigraph<Long, DefaultWeightedEdge> graph;

    public PathFinder(List<Section> sections) {
        this.graph = createGraph(sections);
    }

    public Path findPath(Long sourceId, Long targetId) {
        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        GraphPath shortestPath = null;

        try {
            shortestPath = dijkstraShortestPath.getPath(sourceId, targetId);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("출발역과 종착역이 연결되어 있지 않습니다.");
        }

        if (shortestPath == null) {
            throw new IllegalArgumentException("출발역과 종착역이 연결되어 있지 않습니다.");
        }

        if (shortestPath.getWeight() == 0) {
            throw new IllegalArgumentException("출발역과 종착역이 같습니다.");
        }

        return Path.of(shortestPath.getVertexList(), (int) shortestPath.getWeight());
    }

    private WeightedMultigraph<Long, DefaultWeightedEdge> createGraph(List<Section> sections) {
        final var graph =
                WeightedMultigraph.<Long, DefaultWeightedEdge>builder(DefaultWeightedEdge.class).build();

        sections.stream().forEach(
                section -> {
                    graph.addVertex(section.getUpStationId());
                    graph.addVertex(section.getDownStationId());

                    DefaultWeightedEdge edge = graph.addEdge(section.getUpStationId(), section.getDownStationId());
                    graph.setEdgeWeight(edge, section.getDistance());
                }
        );

        return graph;
    }
}
