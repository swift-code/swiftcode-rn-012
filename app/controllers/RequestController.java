package controllers;

import models.ConnectionRequest;
import models.User;
import play.mvc.Controller;
import play.mvc.Result;

/**
 * Created by lubuntu on 10/23/16.
 */
public class RequestController extends Controller {

    public Result sendRequest(Long sid, Long rid){

        if(sid == null || rid == null || User.find.byId(sid) == null || User.find.byId(rid) == null) {

            return ok();

        }

        else{

            ConnectionRequest cRequest = new ConnectionRequest();
            cRequest.sender.id=sid;
            cRequest.receiver.id=rid;
            cRequest.status=ConnectionRequest.Status.WAITING;
            ConnectionRequest.db().save(cRequest);

        }

        return ok();

    }

    public Result acceptRequest(Long reqid){

        if(reqid == null || ConnectionRequest.find.byId(reqid) == null){
            return ok();
        }

        else{

            ConnectionRequest c = ConnectionRequest.find.byId(reqid);
            c.status=ConnectionRequest.Status.ACCEPTED;

            c.sender.connections.add(c.receiver);
            c.receiver.connections.add(c.sender);
            ConnectionRequest.db().save(c);
            User.db().save(c.sender);
            User.db().save(c.receiver);

        }

        return ok();

    }

}
