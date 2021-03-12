package br.com.api.infrastructure.database.datamodel.tweets;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface TweetRepository extends JpaRepository<Tweet, Long> {

        @Query(value = "SELECT * FROM tweet t "
                        + "WHERE t.id not in (SELECT r.id_tweet FROM referenced_tweet r WHERE r.id_tweet = t.id)",
                        nativeQuery = true)
        public List<Tweet> getAllWithoutReplyRetweet();

        @Query(value = "SELECT * FROM tweet t "
                        + "JOIN recommendation_item ri on t.id = ri.id_tweet "
                        + "JOIN recommendation r on ri.id_recommendation = r.id "
                        + "WHERE ri.rating > 3 and r.finished_date is null and r.id_active_user = ?1",
                        nativeQuery = true)
        public List<Tweet> getRecommendedTweetsByRecommendationType(int idActiveUser);

        @Query(value = "SELECT * FROM tweet t "
                        + "JOIN context_annotation ca on t.id = ca.id_tweet "
                        + "WHERE ca.id_entity in (?2) and t.id_owner_user = t.id_user_timeline and t.is_retweet = 0 and t.sc_score is not null and t.id not in (SELECT rt.id_tweet FROM recommendation_item rt "
                        + "																		   JOIN recommendation r on rt.id_recommendation = r.id "
                        + "																		   WHERE rt.rating is not null and r.id_active_user = ?1 and r.finished_date is null)",
                        nativeQuery = true)
        public List<Tweet> getNotRecommendedTweets(int idActiveUser, List<Long> idsEntities);

        @Query(value = "SELECT * FROM tweet t WHERE t.id IN (?1)", nativeQuery = true)
        public List<Tweet> containsIds(Set<Long> ids);
}
