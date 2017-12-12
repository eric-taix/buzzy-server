package org.jared.quizz.server.resolver;

import com.coxautodev.graphql.tools.GraphQLSubscriptionResolver;
import graphql.schema.DataFetchingEnvironment;
import org.jared.quizz.server.model.Team;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionResolver implements GraphQLSubscriptionResolver {

   /* public Publisher<Team> getTeam(String teamId) {
        System.out.println("Hello");
        return new Publisher<Team>() {
            @Override
            public void subscribe(Subscriber<? super Team> s) {
                s.onSubscribe(new Subscription() {
                    @Override
                    public void request(long n) {
                        )
                    }

                    @Override
                    public void cancel() {

                    }
                });
                System.out.println("John");
            }
        };
    } */

    public Publisher<Team> getTeam(String teamId, DataFetchingEnvironment env) {
        return new TeamPublisher();
    }

  /*  public Team getTeam(String teamId) {
        return new Team().setName("Subscription").setPoints(4);
    } */

}
