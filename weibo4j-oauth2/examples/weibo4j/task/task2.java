package weibo4j.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import weibo4j.Timeline;
import weibo4j.Users;
import weibo4j.examples.oauth2.Log;
import weibo4j.model.StatusWapper;
import weibo4j.model.WeiboException;
import weibo4j.model.PostParameter;
import weibo4j.model.Status;
import weibo4j.model.UserCounts;
import weibo4j.util.ArrayUtils;
import weibo4j.util.WeiboConfig;

import java.io.*;

public class task2 {

	private static String access_token;
	private static String uids;
	private static UserCounts myInfo;

	public static void main(String[] args) throws IOException, ParseException, WeiboException {

		access_token = args[0]; /* it is the access token of me*/
		uids = args[1];

		Users um = new Users(access_token);

		/* get my basic information */
		try {
			List<UserCounts> user = um.getUserCount(uids); /* it is a list of UserCounts */
			myInfo = (UserCounts)user.get(0);              /* we get our basic info*/
		} catch (WeiboException e) {
			e.printStackTrace();
		}


		long nMyfans = myInfo.getFollowersCount();
		long nFriends = myInfo.getFriendsCount();
		long nStatus = myInfo.getStatusesCount();

		/* write down the information */
		FileOutputStream out = new FileOutputStream(new File("/home/wtw/network/task2"));   
		FileOutputStream StatusNum = new FileOutputStream(new File("/home/wtw/network/task2_Status"));   
		out.write(("The basic information for the user " + uids + " is given as\n").getBytes());
		out.write(("The user has " + Long.toString(nMyfans) + " fans\n").getBytes());
		out.write(("The user has " + Long.toString(nFriends) + " friends\n").getBytes());
		out.write(("And the user poorly only publised " + Long.toString(nStatus) + " status\n").getBytes());

		/* the specific information is given */
		out.write(("And the user poorly only publised " + Long.toString(nStatus) + " status\n").getBytes());

		System.out.printf("Sorting the information\n");

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date StartDay = dateFormat.parse("2013-01-01 00:00:00");

		/* get my frequency and one of my friend frequency */
		Map<Integer, Integer> list = getWeiboTime(access_token, uids, StartDay);
		
		for (int key : list.keySet()) {
			System.out.println("key= "+ key + " and value= " + list.get(key));
			StatusNum.write((Integer.toString(key) +"\n").getBytes());
			StatusNum.write((Integer.toString(list.get(key)) +"\n").getBytes());
			out.write(("On the " + Integer.toString(key) + " th day, the user publised ").getBytes());
			out.write((Integer.toString(list.get(key)) + " message\n").getBytes());
		}
		
		/* get my follower and friends location, gender */
		//int mail = getPosition(access_token, uids, StartDay);
		StatusNum.close();
		out.close();
	}

	public static Map<Integer, Integer> getWeiboTime(String access_token, 
			String uids, Date StartDay) throws WeiboException {

		Map<String, String> map = new HashMap<String, String>();  /* it is the parameter map */
		Timeline tm = new Timeline(access_token); /* it is the access time line */
		Map<Integer, Integer> list= new HashMap<Integer, Integer>();	/* it is the answer map, 
																																 recoding the time and number */
		//map.put("uid", uids);
		map.put("count", "200");
		//map.put("page", "2");

		outer: while (true) { /* run until the time dead line met */
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			/* get the time and msg */
			StatusWapper answer = tm.getUserTimeline(map);
			List<Status> msgList = answer.getStatuses();

			/* check the number of msg */
			int msgNum = msgList.size();
			if ((msgNum <=1 & map.get("max_id") != null) | 
					(msgNum <1 & map.get("max_id") == null)) {
				System.out.printf("No more Message for him or her\n");
				break outer; /* no more msg, break */
			} else System.out.printf("%d Message Fetched\n", msgNum);
			
			/* record the msg */
			for (int iNum = 0; iNum < msgNum; iNum ++) {
				if (map.get("max_id") != null & iNum == 0) {
					/* the first one is calculated already */
					continue;
				}
				Status iStatus = msgList.get((int) iNum);  /* get the new msg */
				
				if (iNum == msgNum - 1) {
					String IDstring = iStatus.getId();
					map.put("max_id", IDstring);
				}
				
				/* check for the day */
				Date createdDay = iStatus.getCreatedAt();
				if (createdDay.before(StartDay)){ /* the msg is with in the partition */ 
					break outer;
				}

				/* now test for the time information and merge the msg within one day*/
				int dayDiff = (int) ((createdDay.getTime()-StartDay.getTime())/(24*60*60*1000));
				if (list.get(dayDiff) == null){
					list.put(dayDiff, 1);  /* the day is empty, add one */
				} else list.put(dayDiff, list.get(dayDiff) + 1);  /* already one, add one */
			}

		}
		return list;
	}

}
