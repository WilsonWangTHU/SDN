package weibo4j.task;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import weibo4j.Timeline;
import weibo4j.model.Status;
import weibo4j.model.StatusWapper;
import weibo4j.model.WeiboException;

public class task1 {
	
	//private static int getAllFlag = 0;  /* define two flag variable */
	//private static int msgCount = 200;  /* the maximum number of msg to fetch */
	private static long totalNum = 0;   /* the total number of msg received */
	private static int[] numRecord = new int[22];
	private static int startHour = 0;
	private static int startMin = 0;
	//private static long lastIdLow = 0;
	//private static long lastIdUP = 0;

	private static Map<String, String> map = new HashMap<String, String>();  /* it is the parameter map */
	
	//private static int IDhead = 7;		/* only use the id from the 7th digit */
	public static void main(String[] args) throws IOException {
		map.put("count", "200");
		/* start the program, setting the start time information*/
		//DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	   	Date startDate = new Date();
		//String startString = dateFormat.format(startDate);
		//startHour = (int) Long.parseLong(startString.substring(14, 16));
		//startMin = (int) Long.parseLong(startString.substring(11, 13));
		System.out.printf("The record starts at %s\n", startDate.toString());

		long msgNum = 0;  /* record the number of Message in one fetch */
		/* initialize the access */
		StatusWapper status;
		String access_token= args[0];
		boolean timeExhaust = true;
		int timecount = -1;
		while (timeExhaust) {
			Date currentDate = new Date();
			timecount ++;
			
			//String endString = dateFormat.format(currentDate);
			//int endHour = (int) Long.parseLong(endString.substring(14, 16));
			//int endMin = (int) Long.parseLong(endString.substring(11, 13));
			int minDiff = (int) ((currentDate.getTime()-startDate.getTime())/(60*1000));
			if (minDiff  >= 60) timeExhaust = false;
			
			status = getMsg(access_token);
			List<Status> msgList = status.getStatuses();
			msgNum = msgList.size();
			if (map.get("since_id") != null & msgNum > 0) msgNum = msgNum - 1;
			totalNum = totalNum + msgNum;	
			numRecord[timecount] = (int) msgNum;
			System.out.printf("%d Message Fetched at time %s\n", msgNum,
					currentDate.toString());

			if (msgNum>0){
				Status iStatus = msgList.get((int) 0);  /* get the new msg */
				String IDstring = iStatus.getId();		   /* get the id information */
				map.put("since_id", IDstring);
			}
			//System.out.printf("%d new Message Fetched\n", msgNum - 1);

			/*long newIdLow = Long.parseLong(IDstring.substring(IDhead, IDstring.length()));
				//long newIdUp = Long.parseLong(IDstring.substring(0, IDhead));
				//map.put("count", msgCount);
				if (newIdUp > lastIdUP || (newIdUp == lastIdUP && newIdLow > lastIdLow))
					newMsgNum = newMsgNum + 1;
				else {
					System.out.printf("Last Id is %d%d, now it is %s\n", lastIdUP, lastIdLow, IDstring);
					break;
				}
				*/
				

			/* get ready for the next round */
			//newMsgNum = 0;
			//msgNum = 0;
			
			//Status iStatus = msgList.get(0);
			//String IDstring = iStatus.getId();		   /* get the id information */
			
			/*
			 * lastIdLow = Long.parseLong(IDstring.substring(IDhead, IDstring.length()));
			*	lastIdUP = Long.parseLong(IDstring.substring(0, IDhead));
			*/
			
			System.out.printf("Sleeping !\n");
			System.out.printf("Total %d message! !\n", totalNum);
		    try {
				Thread.sleep(4 * 60000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.printf("Awaking !\n");
		    
		}

		FileOutputStream out = new FileOutputStream(new File("/home/wtw/network/task1"));   
		out.write(("The overal number of status is " + totalNum + "\n").getBytes());
		for (int output = 0; output < 22; output++){
			out.write(("Get number of status is " + numRecord[output] + "\n").getBytes());
		}
		out.close();
	}

	private static StatusWapper getMsg(String access_token) {
		Timeline tm = new Timeline(access_token);
		StatusWapper status = null;
		try {
			status = tm.getFriendsTimeline(map);
			//Log.logInfo(status.toString());
		} catch (WeiboException e) {
			e.printStackTrace();
		}
		if (status == null) {
			System.out.print("reading error!");
			return null;
		}
		else return status;
	}
	
}
