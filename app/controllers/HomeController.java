package controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.ConnectionRequest;
import models.Profile;
import models.User;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.stream.Collectors;

/**
 * Created by lubuntu on 10/23/16.
 */
public class HomeController extends Controller {

    @Inject
    ObjectMapper objectMapper;

    public Result getProfile(Long id){

        User user = User.find.byId(id);
        Profile profile = Profile.find.byId(user.profile.id);

        ObjectNode data = objectMapper.createObjectNode();

        data.put("id",user.id);
        data.put("firstName",profile.firstName);
        data.put("lastName",profile.lastName);
        data.put("company",profile.company);

        data.set("connections", objectMapper.valueToTree(user.connections.stream().map(connection->{

            ObjectNode connectionJson = objectMapper.createObjectNode();

            User connectUser = User.find.byId(connection.id);
            Profile connectProfile = Profile.find.byId(connection.profile.id);

            connectionJson.put("id",connectUser.id);
            connectionJson.put("email",connectUser.email);
            connectionJson.put("firstName",connectProfile.firstName);
            connectionJson.put("lastName",connectProfile.lastName);
            connectionJson.put("company",connectProfile.company);

            return connectionJson;

        }).collect(Collectors.toList()))
        );

        data.set("connectionRequests",objectMapper.valueToTree(user.connectionRequestsReceived.stream().filter(x->x.status.equals(ConnectionRequest.Status.WAITING)).map(connectionRequestRec->{

            ObjectNode connectionReqRecJson = objectMapper.createObjectNode();

            User sendUser = User.find.byId(connectionRequestRec.id);
            Profile senderProfile = Profile.find.byId(connectionRequestRec.sender.profile.id);

            connectionReqRecJson.put("id",connectionRequestRec.id);
            connectionReqRecJson.put("firstName",senderProfile.firstName);
            connectionReqRecJson.put("email",sendUser.email);

            return connectionReqRecJson;

        })));

        data.set("suggestions", objectMapper.valueToTree(User.find.all().stream().filter(x->!user.equals(x)).filter(x->!user.connections.contains(x)).
                filter(x->!user.connectionRequestsReceived.stream().map(y->y.sender).collect(Collectors.toList()).contains(x)).
                filter(x->!user.connectionRequestsSent.stream().map(y->y.receiver).collect(Collectors.toList()).contains(x)).
                map(suggestion->{
                    ObjectNode suggestionJson = objectMapper.createObjectNode();

                    User suggestUser = User.find.byId(suggestion.id);
                    Profile suggestProfile = Profile.find.byId(suggestion.profile.id);

                    suggestionJson.put("id",suggestUser.id);
                    suggestionJson.put("firstName",suggestProfile.firstName);

                    return suggestionJson;

                })
        ));

        return ok(data);

    }



}
