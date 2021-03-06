package com.Repository;

import com.Entity.Room;
import com.Utils.DBConnecter;
import vendor.json.JSONArray;
import vendor.json.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Klasa za dohvaćanje svih podataka vezanih za sobe putem SQL upita
 */
public class RoomRepository implements IRoomRepository {

    ArrayList<Room> rooms;
    JSONObject response = new JSONObject();

    /**
     * Metoda za koja omotaje SQL upit vezan za select jedne sobe određene po parametru room number
     */
    @Override
    public JSONObject viewSingleRoom(JSONObject room) throws SQLException, ClassNotFoundException {

        JSONObject response = null;
        String sql="SELECT * FROM room " +
                "Where room_number='"+room.getString("room_number")+"';";

        ResultSet resultRows = DBConnecter.searchDB(sql);

        // loop through the result set
        if(resultRows.isBeforeFirst()) {
            while (resultRows.next()) {

                response.put("roomNumber", resultRows.getString("room_number"));
                response.put("type", resultRows.getString("type"));
                response.put("capacity", resultRows.getInt("capacity"));
                response.put("phone", resultRows.getString("phone"));

            }
        } else {

            response.put("room", "404 - NotFound");

        }

        return response;

    }

    /**
     * Metoda za koja omotaje SQL upit vezan za select svih soba određenih po uvjetu da nisu zauzete u odbređenom periodu, te većem kapacitetu od zadanog
     */
    @Override
    public JSONArray viewAvailableRooms(JSONObject parameters) throws SQLException, ClassNotFoundException {

            JSONObject room;
            JSONArray rooms = new JSONArray();

//        System.out.println(parameters.getString("dateAndTimeStart") );
//        System.out.println(parameters.getString("dateAndTimeEnd") );

            String sql =
                    "SELECT DISTINCT room.room_number,room.type,room.phone,room.capacity" +
                            " FROM room" +
                            " LEFT JOIN bookings on room.room_number=bookings.room_number" +
                            " WHERE (datetime_start is NULL" +
                            " OR (('" + parameters.getString("dateAndTimeStart") + "' < datetime_start" +
                            " AND '" + parameters.getString("dateAndTimeEnd") + "' < datetime_start)" +
                            " OR ('" + parameters.getString("dateAndTimeStart")  + "' > datetime_end" +
                            " AND '" + parameters.getString("dateAndTimeEnd")  + "' > datetime_end)))" +
                            " AND capacity >= " + parameters.getInt("attendees")  +
                            ";"
                    ;
            ResultSet resultRows = DBConnecter.searchDB(sql);

            if(resultRows.isBeforeFirst()) {
                while (resultRows.next()) {

                    room = new JSONObject();

                    room.put("roomNumber", resultRows.getString("room_number"));
                    room.put("phoneNumber", resultRows.getString("phone"));
                    room.put("roomType", resultRows.getString("type"));
                    room.put("capacity", resultRows.getString("capacity"));

                    rooms.put(room);
                }
            } else {

                room = new JSONObject();

                room.put("room", "404 - NotFound");
                rooms.put(room);

            }

            return rooms;
        }
    }


