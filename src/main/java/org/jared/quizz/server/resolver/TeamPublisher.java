package org.jared.quizz.server.resolver;

import org.jared.quizz.server.model.Team;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

public class TeamPublisher implements Publisher<Team> {
    @Override
    public void subscribe(Subscriber<? super Team> s) {
        s.onNext(new Team().setName("Sub" + System.currentTimeMillis()));
    }
}
