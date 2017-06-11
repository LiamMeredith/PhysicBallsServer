package database;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.physicballs.items.*;
import database.exceptions.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Created by pepe on 29/03/2017.
 */
public class DBHandler {

    EntityManagerFactory emf;
    EntityManager em;

    public DBHandler(EntityManager em) {
        this.em = em;

    }

    public DBHandler() {
        emf = Persistence.createEntityManagerFactory("PhysicBallsServerPU");
    }

    public void insertSpace(String spaceName) {
        DbSpace space = new DbSpace();
        space.setName(spaceName);
        DbSpaceJpaController dbspacejpa = new DbSpaceJpaController(emf);
        try {
            dbspacejpa.create(space);
        } catch (Exception ex) {
            Logger.getLogger(DBHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public List<Object> selectSpace(String spaceName) {//String spaceName
        List<Object> spaceList = new ArrayList<>();

        DbSpaceJpaController dbspacejpa = new DbSpaceJpaController(emf);
        List<Ball> listball = selectBalls(spaceName);
        List<Obstacle> listobstacle = selectObstacles(spaceName);
        List<StopItem> liststopitems = selectStopItems(spaceName);

        spaceList.addAll(listball);
        spaceList.addAll(listobstacle);
        spaceList.addAll(liststopitems);

        return spaceList;
    }

    public void insertBalls(ArrayList<Ball> list) {

        DbSpace dbspace = new DbSpace();
        dbspace = getlastIndexOfSpace();
        System.out.println(dbspace + " Here");
        for (Ball ball : list) {
            DbBall dbBall = new DbBall();
            dbBall.setX(ball.getX());
            dbBall.setY(ball.getY());
            dbBall.setSpeed(ball.getSpeed());
            dbBall.setAccel(ball.getAccel());
            dbBall.setRadius(ball.getRadius());
            dbBall.setAngle(ball.getAngle());
            dbBall.setType(ball.getType().name().charAt(0));
            dbBall.setIdSpace(dbspace.getId());

            DbBallJpaController dbballjpa = new DbBallJpaController(emf);
            dbballjpa.create(dbBall);
        }
    }

    public void insertObstacles(ArrayList<Obstacle> list) {

        DbSpace dbspace = new DbSpace();
        dbspace = getlastIndexOfSpace();

        for (Obstacle obstacle : list) {
            DbObstacle dbobstacle = new DbObstacle();
            dbobstacle.setX(obstacle.getX());
            dbobstacle.setY(obstacle.getY());
            dbobstacle.setWidth(obstacle.getWidth());
            dbobstacle.setHeight(obstacle.getHeight());
            dbobstacle.setIdSpace(dbspace.getId());

            DbObstacleJpaController obstaclejpa = new DbObstacleJpaController(emf);
            obstaclejpa.create(dbobstacle);

        }
    }

    public void insertStopItems(ArrayList<StopItem> list) {

        DbSpace dbspace = new DbSpace();
        dbspace = getlastIndexOfSpace();

        for (StopItem stopItem : list) {
            DbStopitem dbstopitem = new DbStopitem();
            dbstopitem.setX(stopItem.getX());
            dbstopitem.setY(stopItem.getY());
            dbstopitem.setWidth(stopItem.getWidth());
            dbstopitem.setHeight(stopItem.getHeight());
            dbstopitem.setIdSpace(dbspace.getId());

            DbStopitemJpaController dbstopitemjpa = new DbStopitemJpaController(emf);
            try {
                dbstopitemjpa.create(dbstopitem);
            } catch (Exception ex) {
                Logger.getLogger(DBHandler.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    public List<Ball> selectBalls(String spaceName) {
        List<Ball> ballList = new ArrayList<Ball>();
        DbSpaceJpaController spacejpa = new DbSpaceJpaController(emf);
        DbBallJpaController dbballjpa = new DbBallJpaController(emf);
        List<DbBall> list = dbballjpa.findDbBallEntities();

        spacejpa.findDbSpaceEntities().stream().filter(space -> {
            return space.getName().equals(spaceName);
        }).collect(Collectors.toList()).forEach(space -> {
            list.stream().filter(ball -> ball.getIdSpace()==space.getId())
                    .collect(Collectors.toList()).forEach(b ->{
                        Ball ball = new Ball();
                ball.setX(b.getX());
                ball.setY(b.getY());
                ball.setSpeed((float) b.getSpeed(), b.getAngle());
                ball.setAccel(b.getAccel());
                ball.setRadius(b.getRadius());
                //ball.setAngle
                ball.setType(b.getType().toString());

                ballList.add(ball);
                    });
//            for (int i = 0; i < list.size(); i++) {
//                //System.out.println(list.get(i).getIdSpace());  
//                Ball ball = new Ball();
//                ball.setX(list.get(i).getX());
//                ball.setY(list.get(i).getY());
//                ball.setSpeed((float) list.get(i).getSpeed(), list.get(i).getAngle());
//                ball.setAccel(list.get(i).getAccel());
//                ball.setRadius(list.get(i).getRadius());
//                //ball.setAngle
//                ball.setType(list.get(i).getType().toString());
//
//                ballList.add(ball);
//            }
        });

        return ballList;

    }

    public List<Obstacle> selectObstacles(String spaceName) {
        List<Obstacle> obstacleList = new ArrayList<Obstacle>();
        DbSpaceJpaController spacejpa = new DbSpaceJpaController(emf);
        
        DbObstacleJpaController dbobstaclejpa = new DbObstacleJpaController(emf);
        List<DbObstacle> list = dbobstaclejpa.findDbObstacleEntities();

        spacejpa.findDbSpaceEntities().stream().filter(space -> {
            return space.getName().equals(spaceName);
        }).collect(Collectors.toList()).forEach(space -> {
            list.stream().filter(obstacle -> obstacle.getIdSpace()==space.getId())
                    .collect(Collectors.toList()).forEach(o ->{
                        Obstacle obstacle = new Obstacle();

                obstacle.setX(o.getX());
                obstacle.setY(o.getY());
                obstacle.setWidth(o.getWidth());
                obstacle.setHeight(o.getHeight());

                obstacleList.add(obstacle);
                    });
        });

        return obstacleList;
    }

    public List<StopItem> selectStopItems(String spaceName) {
        List<StopItem> stopItemList = new ArrayList<StopItem>();
        DbSpaceJpaController spacejpa = new DbSpaceJpaController(emf);

        DbStopitemJpaController dbstopitemjpa = new DbStopitemJpaController(emf);
        List<DbStopitem> list = dbstopitemjpa.findDbStopitemEntities();

        spacejpa.findDbSpaceEntities().stream().filter(space -> {
            return space.getName().equals(spaceName);
        }).collect(Collectors.toList()).forEach(space -> {
            list.stream().filter(stopItem -> stopItem.getIdSpace()==space.getId())
                    .collect(Collectors.toList()).forEach(s ->{
                        StopItem stopItem = new StopItem();
                stopItem.setX(s.getX());
                stopItem.setY(s.getY());
                stopItem.setWidth(s.getWidth());
                stopItem.setHeight(s.getHeight());
//            stopItem.setParent
                stopItemList.add(stopItem);
                    });
        });
        return stopItemList;
    }

    public List<String> getSpaceList() {
        List<String> list = null;
        DbSpaceJpaController spacejpa = new DbSpaceJpaController(emf);
        List<DbSpace> dbspacelist = spacejpa.findDbSpaceEntities();
        for (int i = 0; i < dbspacelist.size(); i++) {
            String spaceName = dbspacelist.get(i).getName();
            list.add(spaceName);
        }
        return list;
    }

    public String[] getSpaceList1() {

        DbSpaceJpaController spacejpa = new DbSpaceJpaController(emf);
        List<DbSpace> dbspacelist = spacejpa.findDbSpaceEntities();
        String[] list = new String[spacejpa.getDbSpaceCount()];
        for (int i = 0; i < dbspacelist.size(); i++) {
            list[i] = dbspacelist.get(i).getName();
        }
        return list;
    }

    /**
     * This method return true if there isn't a map in the db with the same name
     *
     * @param spaceName
     * @return
     */
    public boolean checkSpaceName(String spaceName) {
        DbSpaceJpaController spacejpa = new DbSpaceJpaController(emf);
        List<DbSpace> list = spacejpa.findDbSpaceEntities();
        for (DbSpace space : list) {
            space.getName();
            if (space.getName().equals(spaceName)) {
                return false;
            }
        }
        return true;
    }

    private DbSpace getlastIndexOfSpace() {

        DbSpaceJpaController dbspacejpa = new DbSpaceJpaController(emf);
        DbSpace dbspace = dbspacejpa.findDbSpaceEntities().get(dbspacejpa.findDbSpaceEntities().size() - 1);
        return dbspace;
    }
}
