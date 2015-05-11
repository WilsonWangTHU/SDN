package weibo4j.task;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import weibo4j.Friendships;
import weibo4j.model.User;
import weibo4j.model.UserWapper;
import weibo4j.model.WeiboException;

public class task4 {

	public static void main(String[] args) throws IOException {
		String access_token = args[0];
		Friendships fm = new Friendships(access_token);
		String screen_name = args[1];
		
		/* use a list to record the */
		Map<Integer, Integer> pro2User= new HashMap<Integer, Integer>();
		Map<Integer, String> pro2Str= new HashMap<Integer, String>();

		int femaleNum = 0;
		int maleNum = 0;
		int friendsNum = 0;
		int followNum= 0;
		int statuesNum = 0;
		int AverageFollowing = 0;
		int AverageFollowMe = 0;

		Map<String, String> map = new HashMap<String, String>();  /* it is the parameter map */
		map.put("count", "200");		
		map.put("screen_name", screen_name);		
		
		try {
			//UserWapper users = fm.getFollowers(map);
			UserWapper users = fm.getFriends(map);
			for(User u : users.getUsers()){
				/* get the city information */
				int proCode = u.getProvince();
				
				if (pro2User.get(proCode) != null) {  /* it is old one*/
					pro2User.put(proCode, pro2User.get(proCode) + 1); /* add one to this city */
				} else {
					pro2User.put(proCode, 1); /* add one to this city */
					String CityName = u.getLocation().substring(0, 2);
					System.out.print(CityName);
					pro2Str.put(proCode, CityName);
				}
				
				/* get the gender information */
				if (u.getGender().equals("f")) femaleNum = femaleNum + 1;
				else if (u.getGender().equals("m")) maleNum = maleNum + 1;
				
				/* get the Friend info */
				friendsNum += u.getFriendsCount();
				followNum += u.getFollowersCount();
				statuesNum += u.getStatusesCount();
				
				/* follow me */
				if (u.isFollowMe()) AverageFollowMe += 1;
				if (u.isFollowing()) AverageFollowing += 1;

			}
			FileOutputStream out = new FileOutputStream(new File("/home/wtw/network/task4"));   
			float userNum = (float)users.getUsers().size();
			out.write(("The number of all friends is " + userNum + "\n").getBytes());
			out.write(("The number of all female is " + femaleNum + "\n").getBytes());
			out.write(("The number of all male is " + maleNum + "\n").getBytes());
			out.write(("The average friends is " + friendsNum / userNum + "\n").getBytes());
			out.write(("The average follow is " + followNum / userNum + "\n").getBytes());
			out.write(("The average statues number is " + statuesNum / userNum + "\n").getBytes());
			out.write(("The average follow me number is " + AverageFollowMe / userNum + "\n").getBytes());
			out.write(("The average following number is " + AverageFollowing / userNum + "\n").getBytes());
			for (int key : pro2User.keySet()) {
				out.write(("The province of " + pro2Str.get(key) + " has " + pro2User.get(key) + "\n").getBytes());
			}

			out.close();
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}

}
