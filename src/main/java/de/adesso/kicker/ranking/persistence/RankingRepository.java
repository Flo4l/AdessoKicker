package de.adesso.kicker.ranking.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RankingRepository extends JpaRepository<Ranking, String> {
    int countAllByRatingAfter(int rating);
}
