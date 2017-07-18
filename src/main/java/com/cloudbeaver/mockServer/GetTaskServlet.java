package com.cloudbeaver.mockServer;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cloudbeaver.checkAndAppend.CheckAndAppendTestConf;
import com.cloudbeaver.checkAndAppend.DatabaseBeanTestConf;
import com.cloudbeaver.checkAndAppend.TableBeanTestConf;
import com.cloudbeaver.client.common.BeaverUtils;
import com.cloudbeaver.client.dbbean.MultiDatabaseBean;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@WebServlet("/api/business/sync/*")
public class GetTaskServlet extends HttpServlet{
	private static Logger logger = LogManager.getLogger("logger");
	private static String getTaskApi = "/api/business/sync/";
	private static MultiDatabaseBean databaseBeans;
	private static String clientId = null;
	private static String PROJECT_ABSOLUTE_PATH = System.getProperty("user.dir");
	public static long now = System.currentTimeMillis();
	public static long fiveDayBefore = (now - now % (24 * 3600 * 1000))- 24 * 3600 * 1000 * 5 - 8 * 3600 * 1000;
	public static long fourDayBefore = (now - now % (24 * 3600 * 1000))- 24 * 3600 * 1000 * 4 - 8 * 3600 * 1000;
	public static String fourDayBeforeString = BeaverUtils.timestampToDateString(fourDayBefore);

	public static Map<String, String> map = new HashMap<String, String>();
	{
		map.put("DocumentDB", "sqlserver");
		map.put("MeetingDB", "webservice");
		map.put("TalkDB", "webservice");
		map.put("PrasDB", "webservice");
		map.put("JfkhDB", "oracle");
		map.put("DocumentDBForSqlite", "sqlite");
		map.put("DocumentFiles", "file");
		map.put("VideoMeetingDB", "sqlserver");
		map.put("HelpDB", "sqlserver");
		map.put("MysqlTest", "mysql");
		map.put("OracleTest", "oracle");
		map.put("SqlServerTest", "sqlserver");
	}

//	public static String documentDBInitJson = "{\"databases\":[{\"db\":\"DocumentDB\",\"rowversion\":\"xgsj2\",\"tables\":"
//			+ "[{\"table\":\"da_jbxx\",\"xgsj\":\"0000000000000000\"},{\"table\":\"da_jl\",\"xgsj\":\"0000000000000000\"},"
//			+ "{\"table\":\"da_qklj\",\"xgsj\":\"0000000000000000\"},{\"table\":\"da_shgx\",\"xgsj\":\"0000000000000000\"},"
//			+ "{\"table\":\"da_tzzb\",\"xgsj\":\"0000000000000000\"},{\"table\":\"da_tszb\",\"join\":[\"da_tsbc\"],\"key\":\"da_tszb.bh=da_tsbc.bh\",\"xgsj\":\"0000000000000000\"},"
//			+ "{\"table\":\"da_clzl\",\"xgsj\":\"0000000000000000\"},{\"table\":\"da_crj\",\"xgsj\":\"0000000000000000\"},"
//			+ "{\"table\":\"da_swdj\",\"xgsj\":\"0000000000000000\"},{\"table\":\"da_tc\",\"xgsj\":\"0000000000000000\"},"
//			+ "{\"table\":\"da_zm\",\"xgsj\":\"0000000000000000\"},{\"table\":\"yzjc\",\"xgsj\":\"0000000000000000\"},"
//			+ "{\"table\":\"xfzb\",\"xgsj\":\"0000000000000000\"},{\"table\":\"djbd\",\"xgsj\":\"0000000000000000\"},"
//			+ "{\"table\":\"db\",\"xgsj\":\"0000000000000000\"},{\"table\":\"nwfg\",\"xgsj\":\"0000000000000000\"},{\"table\":\"hjdd\",\"xgsj\":\"0000000000000000\"},"
//			+ "{\"table\":\"hjbd\",\"xgsj\":\"0000000000000000\"},{\"table\":\"jxcd\",\"xgsj\":\"0000000000000000\"},{\"table\":\"st\",\"xgsj\":\"0000000000000000\"},"
//			+ "{\"table\":\"jy_rzfpbd\",\"xgsj\":\"0000000000000000\"},{\"table\":\"jwbw\",\"xgsj\":\"0000000000000000\"},"
//			+ "{\"table\":\"bwxb\",\"xgsj\":\"0000000000000000\"},{\"table\":\"tt\",\"xgsj\":\"0000000000000000\"},{\"table\":\"lbc\",\"xgsj\":\"0000000000000000\"},"
//			+ "{\"table\":\"ss\",\"join\":[\"ssfb\"],\"key\":\"ss.ssid=ssfb.ssid\",\"xgsj\":\"0000000000000000\"},"
//			+ "{\"table\":\"ks\",\"join\":[\"ksfb\"],\"key\":\"ks.bh=ksfb.bh AND ks.ksrq=ksfb.ksrq\",\"xgsj\":\"0000000000000000\"},"
//			+ "{\"table\":\"jfjjzb\",\"xgsj\":\"0000000000000000\"},{\"table\":\"tbjd\",\"xgsj\":\"0000000000000000\"},{\"table\":\"gzjs\",\"xgsj\":\"0000000000000000\"},"
//			+ "{\"table\":\"qjfj\",\"xgsj\":\"0000000000000000\"},{\"table\":\"yjdj\",\"xgsj\":\"0000000000000000\"},{\"table\":\"sg\",\"xgsj\":\"0000000000000000\"},"
//			+ "{\"table\":\"em_zb\",\"xgsj\":\"0000000000000000\"},{\"table\":\"em_qk\",\"join\":[\"em_zb\"],\"key\":\"em_qk.bh=em_zb.bh\",\"xgsj\":\"0000000000000000\"},"
//			+ "{\"table\":\"em_jd\",\"join\":[\"em_zb\"],\"key\":\"em_jd.bh=em_zb.bh\",\"xgsj\":\"0000000000000000\"},"
//			+ "{\"table\":\"em_jc\",\"join\":[\"em_zb\"],\"key\":\"em_jc.bh=em_zb.bh\",\"xgsj\":\"0000000000000000\"},"
//			+ "{\"table\":\"em_sy\",\"join\":[\"em_zb\"],\"key\":\"em_sy.bh=em_zb.bh\",\"xgsj\":\"0000000000000000\"},"
//			+ "{\"table\":\"fpa_zacy\",\"join\":[\"fpa_zb\",\"fpa_swry\"],\"key\":\"fpa_zacy.ah=fpa_zb.ah AND fpa_zacy.ah=fpa_swry.ah\",\"xgsj\":\"0000000000000000\"},"
//			+ "{\"table\":\"yma_zacy\",\"join\":[\"yma_zb\"],\"key\":\"yma_zacy.ah=yma_zb.ah\",\"xgsj\":\"0000000000000000\"},"
//			+ "{\"table\":\"wjp_bc\",\"join\":[\"wjp_zb\"],\"key\":\"wjp_bc.wjpid=wjp_zb.wjpid\",\"xgsj\":\"0000000000000000\"},"
//			+ "{\"table\":\"wyld_ry\",\"join\":[\"wyld_zb\"],\"key\":\"wyld_ry.wydid=wyld_zb.wydid\",\"xgsj\":\"0000000000000000\"},"
//			+ "{\"table\":\"hj\",\"join\":[\"hj_fb\"],\"key\":\"hj.hjid=hj_fb.hjid\",\"xgsj\":\"0000000000000000\"},{\"table\":\"khjf\",\"xgsj\":\"0000000000000000\"},"
//			+ "{\"table\":\"khjf_sd\",\"xgsj\":\"0000000000000000\"},{\"table\":\"khf\",\"xgsj\":\"0000000000000000\"},{\"table\":\"thdj\",\"xgsj\":\"0000000000000000\"},"
//			+ "{\"table\":\"wp_bgzb\",\"join\":[\"wp_bgbc\"],\"key\":\"wp_bgzb.bh=wp_bgbc.bh AND wp_bgzb.djrq=wp_bgbc.djrq\",\"xgsj\":\"0000000000000000\"},"
//			+ "{\"table\":\"wwzk\",\"xgsj\":\"0000000000000000\"},{\"table\":\"wwjc\",\"xgsj\":\"0000000000000000\"},"
//			+ "{\"table\":\"wwbx\",\"join\":[\"wwzk\"],\"key\":\"wwbx.bh=wwzk.bh AND wwbx.pzrq=wwzk.pzrq\",\"xgsj\":\"0000000000000000\"},{\"table\":\"sndd\",\"xgsj\":\"0000000000000000\"}]}]}";

	public static String documentDBForSqliteInitJson = "{\"databases\":[{\"db\":\"DocumentDBForSqlite\",\"rowversion\":\"xgsj2\",\"tables\":"
			+ "[{\"table\":\"da_jbxx\",\"xgsj\":\"0\"},{\"table\":\"da_jl\",\"xgsj\":\"0\"},{\"table\":\"da_qklj\",\"xgsj\":\"0\"},{\"table\":\"da_shgx\",\"xgsj\":\"0\"},"
			+ "{\"table\":\"da_tzzb\",\"xgsj\":\"0\"},{\"table\":\"da_tszb\",\"join\":[\"da_tsbc\"],\"key\":\"da_tszb.bh=da_tsbc.bh\",\"xgsj\":\"0\"},"
			+ "{\"table\":\"bwxb\",\"xgsj\":\"0\"},{\"table\":\"tt\",\"xgsj\":\"0\"},{\"table\":\"lbc\",\"xgsj\":\"0\"},{\"table\":\"ss\",\"join\":[\"ssfb\"],\"key\":"
			+ "\"ss.ssid=ssfb.ssid\",\"xgsj\":\"0\"},{\"table\":\"ks\",\"join\":[\"ksfb\"],\"key\":\"ks.bh=ksfb.bh AND ks.ksrq=ksfb.ksrq\",\"xgsj\":\"0\"},"
			+ "{\"table\":\"jfjjzb\",\"xgsj\":\"0\"},{\"table\":\"tbjd\",\"xgsj\":\"0\"},{\"table\":\"gzjs\",\"xgsj\":\"0\"},{\"table\":\"qjfj\",\"xgsj\":\"0\"},"
			+ "{\"table\":\"yjdj\",\"xgsj\":\"0\"},{\"table\":\"sg\",\"xgsj\":\"0\"},{\"table\":\"em_zb\",\"xgsj\":\"0\"},{\"table\":\"em_qk\",\"join\":[\"em_zb\"],\"key\":"
			+ "\"em_qk.bh=em_zb.bh\",\"xgsj\":\"0\"},{\"table\":\"em_jd\",\"join\":[\"em_zb\"],\"key\":\"em_jd.bh=em_zb.bh\",\"xgsj\":\"0\"},{\"table\":\"em_jc\",\"join\":"
			+ "[\"em_zb\"],\"key\":\"em_jc.bh=em_zb.bh\",\"xgsj\":\"0\"},{\"table\":\"em_sy\",\"join\":[\"em_zb\"],\"key\":\"em_sy.bh=em_zb.bh\",\"xgsj\":\"0\"},"
			+ "{\"table\":\"fpa_zacy\",\"join\":[\"fpa_zb\",\"fpa_swry\"],\"key\":\"fpa_zacy.ah=fpa_zb.ah AND fpa_zacy.ah=fpa_swry.ah\",\"xgsj\":\"0\"},"
			+ "{\"table\":\"yma_zacy\",\"join\":[\"yma_zb\"],\"key\":\"yma_zacy.ah=yma_zb.ah\",\"xgsj\":\"0\"},{\"table\":\"wjp_bc\",\"join\":[\"wjp_zb\"],\"key\":"
			+ "\"wjp_bc.wjpid=wjp_zb.wjpid\",\"xgsj\":\"0\"},{\"table\":\"wyld_ry\",\"join\":[\"wyld_zb\"],\"key\":\"wyld_ry.wydid=wyld_zb.wydid\",\"xgsj\":\"0\"},"
			+ "{\"table\":\"hj\",\"join\":[\"hj_fb\"],\"key\":\"hj.hjid=hj_fb.hjid\",\"xgsj\":\"0\"},{\"table\":\"khjf\",\"xgsj\":\"0\"},{\"table\":\"khjf_sd\",\"xgsj\":\"0\"},"
			+ "{\"table\":\"khf\",\"xgsj\":\"0\"},{\"table\":\"thdj\",\"xgsj\":\"0\"},{\"table\":\"wp_bgzb\",\"join\":[\"wp_bgbc\"],\"key\":"
			+ "\"wp_bgzb.bh=wp_bgbc.bh AND wp_bgzb.djrq=wp_bgbc.djrq\",\"xgsj\":\"0\"},{\"table\":\"wwzk\",\"xgsj\":\"0\"},{\"table\":\"wwjc\",\"xgsj\":\"0\"},"
			+ "{\"table\":\"wwbx\",\"join\":[\"wwzk\"],\"key\":\"wwbx.bh=wwzk.bh AND wwbx.pzrq=wwzk.pzrq\",\"xgsj\":\"0\"},{\"table\":\"sndd\",\"xgsj\":\"0\"}]}]}";

//	public static String documentDBInitJson = "{\"databases\":[{\"db\":\"DocumentDB\",\"rowversion\":\"xgsj\",\"tables\":"
//		+ "[{\"table\":\"da_jbxx\",\"xgsj\":\"0\"},{\"table\":\"daFILE_UPLOAD_RETRY_TIMES_jl\",\"xgsj\":\"0\"},{\"table\":\"da_qklj\",\"xgsj\":\"0\"},{\"table\":\"da_shgx\",\"xgsj\":\"0\"},"
//		+ "{\"table\":\"da_tzzb\",\"xgsj\":\"0\"},{\"table\":\"da_tszb\",\"join\":[\"da_tsbc\"],\"key\":\"da_tszb.bh=da_tsbc.bh\",\"xgsj\":\"0\"},"
//		+ "{\"table\":\"bwxb\",\"xgsj\":\"0\"},{\"table\":\"tt\",\"xgsj\":\"0\"},{\"table\":\"lbc\",\"xgsj\":\"0\"},{\"table\":\"ss\",\"join\":[\"ssfb\"],\"key\":"
//		+ "\"ss.ssid=ssfb.ssid\",\"xgsj\":\"0\"},{\"table\":\"ks\",\"join\":[\"ksfb\"],\"key\":\"ks.bh=ksfb.bh AND ks.ksrq=ksfb.ksrq\",\"xgsj\":\"0\"},"
//		+ "{\"table\":\"jfjjzb\",\"xgsj\":\"0\"},{\"table\":\"tbjd\",\"xgsj\":\"0\"},{\"table\":\"gzjs\",\"xgsj\":\"0\"},{\"table\":\"qjfj\",\"xgsj\":\"0\"},"
//		+ "{\"table\":\"yjdj\",\"xgsj\":\"0\"},{\"table\":\"sg\",\"xgsj\":\"0\"},{\"table\":\"em_zb\",\"xgsj\":\"0\"},{\"table\":\"em_qk\",\"join\":[\"em_zb\"],\"key\":"
//		+ "\"em_qk.bh=em_zb.bh\",\"xgsj\":\"0\"},{\"table\":\"em_jd\",\"join\":[\"em_zb\"],\"key\":\"em_jd.bh=em_zb.bh\",\"xgsj\":\"0\"},{\"table\":\"em_jc\",\"join\":"
//		+ "[\"em_zb\"],\"key\":\"em_jc.bh=em_zb.bh\",\"xgsj\":\"0\"},{\"table\":\"em_sy\",\"join\":[\"em_zb\"],\"key\":\"em_sy.bh=em_zb.bh\",\"xgsj\":\"0\"},"
//		+ "{\"table\":\"fpa_zacy\",\"join\":[\"fpa_zb\",\"fpa_swry\"],\"key\":\"fpa_zacy.ah=fpa_zb.ah AND fpa_zacy.ah=fpa_swry.ah\",\"xgsj\":\"0\"},"
//		+ "{\"table\":\"yma_zacy\",\"join\":[\"yma_zb\"],\"key\":\"yma_zacy.ah=yma_zb.ah\",\"xgsj\":\"0\"},{\"table\":\"wjp_bc\",\"join\":[\"wjp_zb\"],\"key\":"
//		+ "\"wjp_bc.wjpid=wjp_zb.wjpid\",\"xgsj\":\"0\"},{\"table\":\"wyld_ry\",\"join\":[\"wyld_zb\"],\"key\":\"wyld_ry.wydid=wyld_zb.wydid\",\"xgsj\":\"0\"},"
//		+ "{\"table\":\"hj\",\"join\":[\"hj_fb\"],\"key\":\"hj.hjid=hj_fb.hjid\",\"xgsj\":\"0\"},{\"table\":\"khjf\",\"xgsj\":\"0\"},{\"table\":\"khjf_sd\",\"xgsj\":\"0\"},"
//		+ "{\"table\":\"khf\",\"xgsj\":\"0\"},{\"table\":\"thdj\",\"xgsj\":\"0\"},{\"table\":\"wp_bgzb\",\"join\":[\"wp_bgbc\"],\"key\":"
//		+ "\"wp_bgzb.bh=wp_bgbc.bh AND wp_bgzb.djrq=wp_bgbc.djrq\",\"xgsj\":\"0\"},{\"table\":\"wwzk\",\"xgsj\":\"0\"},{\"table\":\"wwjc\",\"xgsj\":\"0\"},"
//		+ "{\"table\":\"wwbx\",\"join\":[\"wwzk\"],\"key\":\"wwbx.bh=wwzk.bh AND wwbx.pzrq=wwzk.pzrq\",\"xgsj\":\"0\"},{\"table\":\"sndd\",\"xgsj\":\"0\"}]},"
//		+ "{\"db\":\"MeetingDB\",\"rowversion\":\"starttime\",\"tables\":[{\"table\":\"pias/getItlist\",\"starttime\":\"" + fiveDayBefore + "\"}]},"
//		+ "{\"db\":\"TalkDB\",\"rowversion\":\"starttime\",\"tables\":[{\"table\":\"qqdh/getTalklist\",\"starttime\":\"" + fiveDayBefore + "\"},{\"table\":\"qqdh/getQqdh\",\"starttime\":\"" + fiveDayBefore + "\"}]},"
//		+ "{\"db\":\"PrasDB\",\"rowversion\":\"starttime\",\"tables\":[{\"table\":\"pras/getResult\",\"starttime\":\"" + fiveDayBefore + "\"},{\"table\":\"pras/getTable\",\"starttime\":\"" + fiveDayBefore + "\"}]},"
//		+ "{\"db\":\"JfkhDB\",\"rowversion\":\"ID\",\"tables\":[{\"table\":\"BZ_JFKH_DRECORDSUB\",\"join_subtable\":[\"BZ_JFKH_DRECORD\"],\"key\":"
//		+ "\"BZ_JFKH_DRECORDSUB.PID=BZ_JFKH_DRECORD.ID\",\"ID\":\"0\"},{\"table\":\"BZ_JFKH_MYZKJFSPSUB\",\"join_subtable\":[\"BZ_JFKH_MYZKJFSP\"],\"key\":"
//		+ "\"BZ_JFKH_MYZKJFSPSUB.PID=BZ_JFKH_MYZKJFSP.ID\",\"ID\":\"0\"},{\"table\":\"BZ_JFKH_ZFFJQDDJL\",\"ID\":\"0\"},{\"table\":\"BZ_JFKH_ZFFYDDJL\",\"ID\":\"0\"},"
//		+ "{\"table\":\"BZ_KHBZ_DOCTOR\",\"join_subtable\":[\"BZ_KHBZ_DOCTORSUB\"],\"key\":\"BZ_KHBZ_DOCTOR.ID=BZ_KHBZ_DOCTORSUB.PID\",\"ID\":\"0\"},"
//		+ "{\"table\":\"BZ_KHBZ_JBSP\",\"ID\":\"0\"},{\"table\":\"BZ_KHBZ_JJJSP\",\"ID\":\"0\"},{\"table\":\"BZ_KHBZ_LJTQSP\",\"join_subtable\":[\"BZ_KHBZ_LJTQSPSUB\"],"
//		+ "\"key\":\"BZ_KHBZ_LJTQSP.ID=BZ_KHBZ_LJTQSPSUB.PID\",\"ID\":\"0\"},{\"table\":\"BZ_KHBZ_TXLJTQSP\",\"ID\":\"0\"},{\"table\":\"BZ_KHBZ_XZCFSP\",\"ID\":\"0\"},"
//		+ "{\"table\":\"BZ_KHBZ_XZJLSP\",\"ID\":\"0\"}]}]}";


	public static String changeDocumentDBXgsjInitJson = "{\"databases\":[{\"db\":\"DocumentDB\",\"rowversion\":\"xgsj\",\"tables\":"
			+ "[{\"table\":\"da_jbxx\",\"xgsj\":\"0000000000000005\"},{\"table\":\"da_jl\",\"xgsj\":\"0000000000000005\"},{\"table\":\"da_qklj\",\"xgsj\":\"0000000000000005\"},{\"table\":\"da_shgx\",\"xgsj\":\"0000000000000005\"},"
			+ "{\"table\":\"da_tzzb\",\"xgsj\":\"0000000000000005\"},{\"table\":\"da_tszb\",\"join\":[\"da_tsbc\"],\"key\":\"da_tszb.bh=da_tsbc.bh\",\"xgsj\":\"0000000000000005\"},"
			+ "{\"table\":\"bwxb\",\"xgsj\":\"0000000000000005\"},{\"table\":\"tt\",\"xgsj\":\"0000000000000005\"},{\"table\":\"lbc\",\"xgsj\":\"0000000000000005\"},{\"table\":\"ss\",\"join\":[\"ssfb\"],\"key\":"
			+ "\"ss.ssid=ssfb.ssid\",\"xgsj\":\"0000000000000005\"},{\"table\":\"ks\",\"join\":[\"ksfb\"],\"key\":\"ks.bh=ksfb.bh AND ks.ksrq=ksfb.ksrq\",\"xgsj\":\"0000000000000005\"},"
			+ "{\"table\":\"jfjjzb\",\"xgsj\":\"0000000000000005\"},{\"table\":\"tbjd\",\"xgsj\":\"0000000000000005\"},{\"table\":\"gzjs\",\"xgsj\":\"0000000000000005\"},{\"table\":\"qjfj\",\"xgsj\":\"0000000000000005\"},"
			+ "{\"table\":\"yjdj\",\"xgsj\":\"0000000000000005\"},{\"table\":\"sg\",\"xgsj\":\"0000000000000005\"},{\"table\":\"em_zb\",\"xgsj\":\"0000000000000005\"},{\"table\":\"em_qk\",\"join\":[\"em_zb\"],\"key\":"
			+ "\"em_qk.bh=em_zb.bh\",\"xgsj\":\"0000000000000005\"},{\"table\":\"em_jd\",\"join\":[\"em_zb\"],\"key\":\"em_jd.bh=em_zb.bh\",\"xgsj\":\"0000000000000005\"},{\"table\":\"em_jc\",\"join\":"
			+ "[\"em_zb\"],\"key\":\"em_jc.bh=em_zb.bh\",\"xgsj\":\"0000000000000005\"},{\"table\":\"em_sy\",\"join\":[\"em_zb\"],\"key\":\"em_sy.bh=em_zb.bh\",\"xgsj\":\"0000000000000005\"},"
			+ "{\"table\":\"fpa_zacy\",\"join\":[\"fpa_zb\",\"fpa_swry\"],\"key\":\"fpa_zacy.ah=fpa_zb.ah AND fpa_zacy.ah=fpa_swry.ah\",\"xgsj\":\"0000000000000005\"},"
			+ "{\"table\":\"yma_zacy\",\"join\":[\"yma_zb\"],\"key\":\"yma_zacy.ah=yma_zb.ah\",\"xgsj\":\"0000000000000005\"},{\"table\":\"wjp_bc\",\"join\":[\"wjp_zb\"],\"key\":"
			+ "\"wjp_bc.wjpid=wjp_zb.wjpid\",\"xgsj\":\"0000000000000005\"},{\"table\":\"wyld_ry\",\"join\":[\"wyld_zb\"],\"key\":\"wyld_ry.wydid=wyld_zb.wydid\",\"xgsj\":\"0000000000000005\"},"
			+ "{\"table\":\"hj\",\"join\":[\"hj_fb\"],\"key\":\"hj.hjid=hj_fb.hjid\",\"xgsj\":\"0000000000000005\"},{\"table\":\"khjf\",\"xgsj\":\"0000000000000005\"},{\"table\":\"khjf_sd\",\"xgsj\":\"0000000000000005\"},"
			+ "{\"table\":\"khf\",\"xgsj\":\"0000000000000005\"},{\"table\":\"thdj\",\"xgsj\":\"0000000000000005\"},{\"table\":\"wp_bgzb\",\"join\":[\"wp_bgbc\"],\"key\":"
			+ "\"wp_bgzb.bh=wp_bgbc.bh AND wp_bgzb.djrq=wp_bgbc.djrq\",\"xgsj\":\"0000000000000005\"},{\"table\":\"wwzk\",\"xgsj\":\"0000000000000005\"},{\"table\":\"wwjc\",\"xgsj\":\"0000000000000005\"},"
			+ "{\"table\":\"wwbx\",\"join\":[\"wwzk\"],\"key\":\"wwbx.bh=wwzk.bh AND wwbx.pzrq=wwzk.pzrq\",\"xgsj\":\"0000000000000005\"},{\"table\":\"sndd\",\"xgsj\":\"0000000000000005\"}]},"
			+ "{\"db\":\"MeetingDB\",\"rowversion\":\"starttime\",\"tables\":[{\"table\":\"pias/getItlist\",\"starttime\":\"" + fiveDayBefore + "\"}]},"
			+ "{\"db\":\"TalkDB\",\"rowversion\":\"starttime\",\"tables\":[{\"table\":\"qqdh/getTalklist\",\"starttime\":\"" + fiveDayBefore + "\"},{\"table\":\"qqdh/getQqdh\",\"starttime\":\"" + fiveDayBefore + "\"}]},"
			+ "{\"db\":\"PrasDB\",\"rowversion\":\"starttime\",\"tables\":[{\"table\":\"pras/getResult\",\"starttime\":\"" + fiveDayBefore + "\"},{\"table\":\"pras/getTable\",\"starttime\":\"" + fiveDayBefore + "\"}]},"
			+ "{\"db\":\"JfkhDB\",\"rowversion\":\"ID\",\"tables\":[{\"table\":\"BZ_JFKH_DRECORDSUB\",\"join\":[\"BZ_JFKH_DRECORD\"],\"key\":\"BZ_JFKH_DRECORDSUB.PID=BZ_JFKH_DRECORD.ID\",\"ID\":\"0\"},"
			+ "{\"table\":\"BZ_JFKH_MYZKJFSPSUB\",\"join\":[\"BZ_JFKH_MYZKJFSP\"],\"key\":\"BZ_JFKH_MYZKJFSPSUB.PID=BZ_JFKH_MYZKJFSP.ID\",\"ID\":\"0\"},{\"table\":\"BZ_JFKH_ZFFJQDDJL\",\"ID\":\"0\"},"
			+ "{\"table\":\"BZ_JFKH_ZFFYDDJL\",\"ID\":\"0\"},{\"table\":\"BZ_KHBZ_DOCTOR\",\"join_subtable\":[\"BZ_KHBZ_DOCTORSUB\"],\"key\":\"BZ_KHBZ_DOCTOR.ID=BZ_KHBZ_DOCTORSUB.PID\",\"ID\":\"0\"},"
			+ "{\"table\":\"BZ_KHBZ_JBSP\",\"ID\":\"0\"},{\"table\":\"BZ_KHBZ_JJJSP\",\"ID\":\"0\"},{\"table\":\"BZ_KHBZ_LJTQSP\",\"join_subtable\":[\"BZ_KHBZ_LJTQSPSUB\"],\"key\":\"BZ_KHBZ_LJTQSP.ID=BZ_KHBZ_LJTQSPSUB.PID\",\"ID\":\"0\"},"
			+ "{\"table\":\"BZ_KHBZ_TXLJTQSP\",\"ID\":\"0\"},{\"table\":\"BZ_KHBZ_XZCFSP\",\"ID\":\"0\"},{\"table\":\"BZ_KHBZ_XZJLSP\",\"ID\":\"0\"}]},"
			+ "{\"db\":\"DocumentDBForSqlite\",\"rowversion\":\"xgsj2\",\"tables\":"
			+ "[{\"table\":\"da_jbxx\",\"xgsj\":\"0\"},{\"table\":\"da_jl\",\"xgsj\":\"0\"},{\"table\":\"da_qklj\",\"xgsj\":\"0\"},{\"table\":\"da_shgx\",\"xgsj\":\"0\"},"
			+ "{\"table\":\"da_tzzb\",\"xgsj\":\"0\"},{\"table\":\"da_tszb\",\"join\":[\"da_tsbc\"],\"key\":\"da_tszb.bh=da_tsbc.bh\",\"xgsj\":\"0\"},"
			+ "{\"table\":\"bwxb\",\"xgsj\":\"0\"},{\"table\":\"tt\",\"xgsj\":\"0\"},{\"table\":\"lbc\",\"xgsj\":\"0\"},{\"table\":\"ss\",\"join\":[\"ssfb\"],\"key\":"
			+ "\"ss.ssid=ssfb.ssid\",\"xgsj\":\"0\"},{\"table\":\"ks\",\"join\":[\"ksfb\"],\"key\":\"ks.bh=ksfb.bh AND ks.ksrq=ksfb.ksrq\",\"xgsj\":\"0\"},"
			+ "{\"table\":\"jfjjzb\",\"xgsj\":\"0\"},{\"table\":\"tbjd\",\"xgsj\":\"0\"},{\"table\":\"gzjs\",\"xgsj\":\"0\"},{\"table\":\"qjfj\",\"xgsj\":\"0\"},"
			+ "{\"table\":\"yjdj\",\"xgsj\":\"0\"},{\"table\":\"sg\",\"xgsj\":\"0\"},{\"table\":\"em_zb\",\"xgsj\":\"0\"},{\"table\":\"em_qk\",\"join\":[\"em_zb\"],\"key\":"
			+ "\"em_qk.bh=em_zb.bh\",\"xgsj\":\"0\"},{\"table\":\"em_jd\",\"join\":[\"em_zb\"],\"key\":\"em_jd.bh=em_zb.bh\",\"xgsj\":\"0\"},{\"table\":\"em_jc\",\"join\":"
			+ "[\"em_zb\"],\"key\":\"em_jc.bh=em_zb.bh\",\"xgsj\":\"0\"},{\"table\":\"em_sy\",\"join\":[\"em_zb\"],\"key\":\"em_sy.bh=em_zb.bh\",\"xgsj\":\"0\"},"
			+ "{\"table\":\"fpa_zacy\",\"join\":[\"fpa_zb\",\"fpa_swry\"],\"key\":\"fpa_zacy.ah=fpa_zb.ah AND fpa_zacy.ah=fpa_swry.ah\",\"xgsj\":\"0\"},"
			+ "{\"table\":\"yma_zacy\",\"join\":[\"yma_zb\"],\"key\":\"yma_zacy.ah=yma_zb.ah\",\"xgsj\":\"0\"},{\"table\":\"wjp_bc\",\"join\":[\"wjp_zb\"],\"key\":"
			+ "\"wjp_bc.wjpid=wjp_zb.wjpid\",\"xgsj\":\"0\"},{\"table\":\"wyld_ry\",\"join\":[\"wyld_zb\"],\"key\":\"wyld_ry.wydid=wyld_zb.wydid\",\"xgsj\":\"0\"},"
			+ "{\"table\":\"hj\",\"join\":[\"hj_fb\"],\"key\":\"hj.hjid=hj_fb.hjid\",\"xgsj\":\"0\"},{\"table\":\"khjf\",\"xgsj\":\"0\"},{\"table\":\"khjf_sd\",\"xgsj\":\"0\"},"
			+ "{\"table\":\"khf\",\"xgsj\":\"0\"},{\"table\":\"thdj\",\"xgsj\":\"0\"},{\"table\":\"wp_bgzb\",\"join\":[\"wp_bgbc\"],\"key\":"
			+ "\"wp_bgzb.bh=wp_bgbc.bh AND wp_bgzb.djrq=wp_bgbc.djrq\",\"xgsj\":\"0\"},{\"table\":\"wwzk\",\"xgsj\":\"0\"},{\"table\":\"wwjc\",\"xgsj\":\"0\"},"
			+ "{\"table\":\"wwbx\",\"join\":[\"wwzk\"],\"key\":\"wwbx.bh=wwzk.bh AND wwbx.pzrq=wwzk.pzrq\",\"xgsj\":\"0\"},{\"table\":\"sndd\",\"xgsj\":\"0\"}]},"
			+ "{\"db\":\"VideoMeetingDB\",\"rowversion\":\"ID\",\"tables\":"
			+ "[{\"table\":\"MeetingApplies\",\"ID\":\"" + 0 + "\", \"join\":[\"UserAccounts\", \"Prisoner\", \"Users\", \"Departments\", \"Jails\"],"
			+ "\"key\":\" MeetingApplies.PrisonerFk = Prisoner.UserFk and MeetingApplies.CreateUserFk = UserAccounts.Id and Prisoner.UserFk = Users.Id and Users.DepartmentFk = Departments.Id and MeetingApplies.MeetingJailFk = Jails.Id\"}]},"
			+ "{\"db\":\"HelpDB\",\"rowversion\":\"ID\",\"tables\":"
			+ "[{\"table\":\"Fee_UserCharges\",\"ID\":\"" + 0 + "\", \"join\":[\"Fee_UserAccounts\", \"Users\", \"Departments\"],"
			+ "\"key\":\"  Fee_UserCharges.UserFk = Users.Id and Fee_UserCharges.UserAccountFk = Fee_UserAccounts.Id and Users.DepartmentFk = Departments.Id\"},"
			+ "{\"table\":\"Fee_UserDeductions\",\"ID\":\"" + 0 + "\", \"join\":[\"Users\", \"Departments\"],\"key\":\"Fee_UserDeductions.UserFk = Users.Id and Users.DepartmentFk = Departments.Id\"},"
			+ "{\"table\":\"Contacts\",\"ID\":\"" + 0 + "\", \"join\":[\"Prisoner\", \"Users\", \"Departments\", \"CommonCodes\"],"
			+ "\"key\":\"Contacts.PrisonerFk = Prisoner.UserFk and Prisoner.UserFk = Users.Id and Contacts.RelationFk = CommonCodes.Id and Users.DepartmentFk = Departments.Id\"},"
			+ "{\"table\":\"Sms_SmsSendBoxes\",\"ID\":\"" + 0 + "\", \"join\":[\"Prisoner\", \"Users\", \"Departments\", \"CommonCodes\"],"
			+ "\"key\":\"Sms_SmsSendBoxes.PrisonerFk = Prisoner.UserFk and Prisoner.UserFk = Users.Id and Sms_SmsSendBoxes.FailureReasonFk = CommonCodes.Id and Sms_SmsSendBoxes.FeelingFk = CommonCodes.Id "
			+ "and Sms_SmsSendBoxes.RelationFk = CommonCodes.Id and Users.DepartmentFk = Departments.Id\"},"
			+ "{\"table\":\"Sms_SmsReceiveBoxes\",\"ID\":\"" + 0 + "\", \"join\":[\"Prisoner\", \"Users\", \"Departments\", \"CommonCodes\"],"
			+ "\"key\":\"Sms_SmsReceiveBoxes.PrisonerFk = Prisoner.UserFk and Prisoner.UserFk = Users.Id and Sms_SmsReceiveBoxes.FailureReasonFk = CommonCodes.Id "
			+ "and Sms_SmsReceiveBoxes.RelationFk = CommonCodes.Id and Users.DepartmentFk = Departments.Id\"},"
			+ "{\"table\":\"ClassRoom_Histories\",\"ID\":\"" + 0 + "\", \"join\":[\"ClassRoom_Videos\", \"Users\", \"Departments\", \"CommonCodes\"],"
			+ "\"key\":\"ClassRoom_Histories.UserFk = Users.Id and ClassRoom_Histories.VideoFk = ClassRoom_Videos.Id and ClassRoom_Videos.TypeFk = CommonCodes.Id and Users.DepartmentFk = Departments.Id\"},"
			+ "{\"table\":\"ClassRoom_StudyNotes\",\"ID\":\"" + 0 + "\", \"join\":[\"ClassRoom_Videos\", \"Users\", \"Departments\", \"CommonCodes\"],"
			+ "\"key\":\"ClassRoom_StudyNotes.UserFk = Users.Id and ClassRoom_StudyNotes.VideoFk = ClassRoom_Videos.Id and ClassRoom_Videos.TypeFk = CommonCodes.Id and Users.DepartmentFk = Departments.Id\"},"
			+ "{\"table\":\"Library_BookHistories\",\"ID\":\"" + 0 + "\", \"join\":[\"Library_Books\", \"Users\", \"Departments\", \"CommonCodes\"],"
			+ "\"key\":\"Library_BookHistories.UserFk = Users.Id and Library_BookHistories.BookFk = Library_Books.Id and Library_Books.CatalogFk = CommonCodes.Id and Users.DepartmentFk = Departments.Id\"},"
			+ "{\"table\":\"Library_BookReviews\",\"ID\":\"" + 0 + "\", \"join\":[\"Library_Books\", \"Users\", \"Departments\", \"CommonCodes\"],"
			+ "\"key\":\"Library_BookReviews.UserFk = Users.Id and Library_BookReviews.BookFk = Library_Books.Id and Library_Books.CatalogFk = CommonCodes.Id and Users.DepartmentFk = Departments.Id\"},"
			+ "{\"table\":\"Vod_VideoHistories\",\"ID\":\"" + 0 + "\", \"join\":[\"Vod_Videos\", \"Users\", \"Departments\", \"CommonCodes\"],"
			+ "\"key\":\"Vod_VideoHistories.UserFk = Users.Id and Vod_VideoHistories.VideoFk = Vod_Videos.Id and Vod_Videos.CatalogFk = CommonCodes.Id and Users.DepartmentFk = Departments.Id\"},"
			+ "{\"table\":\"Vod_VideoReviews\",\"ID\":\"" + 0 + "\", \"join\":[\"Vod_Videos\", \"Users\", \"Departments\", \"CommonCodes\"],"
			+ "\"key\":\"Vod_VideoReviews.UserFk = Users.Id and Vod_VideoReviews.VideoFk = Vod_Videos.Id and Vod_Videos.CatalogFk = CommonCodes.Id and Users.DepartmentFk = Departments.Id\"},"
			+ "{\"table\":\"Terminal_TerminalApplications\",\"ID\":\"" + 0 + "\", \"join_sub\":[\"Terminal_SubscribeInfos\", \"TerminalApplications_SubscribeInfos\", "
			+ "\"Terminal_TerminalInfos\", \"Terminal_TerminalModels\", \"Users\", \"Departments\"],"
			+ "\"key\":\"Terminal_TerminalApplications.UserFk = Users.Id and Terminal_TerminalApplications.TerminalFk = Terminal_TerminalInfos.Id "
			+ "and Terminal_TerminalApplications.TerminalModelFk = Terminal_TerminalModels.Id and TerminalApplications_SubscribeInfos.SubscribeInfoFk = Terminal_SubscribeInfos.Id "
			+ "and TerminalApplications_SubscribeInfos.TerminalApplicationFk = Terminal_TerminalApplications.Id and Users.DepartmentFk = Departments.Id\"},"
			+ "{\"table\":\"Messages\",\"ID\":\"" + 0 + "\", \"join\":[\"Users\", \"Departments\"],\"key\":\"Messages.UserFk = Users.Id and Users.DepartmentFk = Departments.Id\"},"
			+ "{\"table\":\"RainGlass_UserEmotions\",\"ID\":\"" + 0 + "\", \"join\":[\"RainGlass_Emotions\", \"RainGlass_EmotionPersuasions\", \"Users\", \"Departments\"],"
			+ "\"key\":\"RainGlass_UserEmotions.UserFk = Users.Id and RainGlass_UserEmotions.EmotionFk = RainGlass_Emotions.Id "
			+ "and RainGlass_Emotions.Id = RainGlass_EmotionPersuasions.EmotionFk and Users.DepartmentFk = Departments.Id\"}]}]}";

	public static String AllDBInitJson = "{\"databases\":[{\"db\":\"DocumentDB\",\"rowversion\":\"xgsj\",\"tables\":"
			+ "[{\"table\":\"da_jbxx\",\"xgsj\":\"0\"},{\"table\":\"da_jl\",\"xgsj\":\"0\"},{\"table\":\"da_qklj\",\"xgsj\":\"0\"},{\"table\":\"da_shgx\",\"xgsj\":\"0\"},"
			+ "{\"table\":\"da_tzzb\",\"xgsj\":\"0\"},{\"table\":\"da_tszb\",\"join\":[\"da_tsbc\"],\"key\":\"da_tszb.bh=da_tsbc.bh\",\"xgsj\":\"0\"},"
			+ "{\"table\":\"bwxb\",\"xgsj\":\"0\"},{\"table\":\"tt\",\"xgsj\":\"0\"},{\"table\":\"lbc\",\"xgsj\":\"0\"},{\"table\":\"ss\",\"join\":[\"ssfb\"],\"key\":"
			+ "\"ss.ssid=ssfb.ssid\",\"xgsj\":\"0\"},{\"table\":\"ks\",\"join\":[\"ksfb\"],\"key\":\"ks.bh=ksfb.bh AND ks.ksrq=ksfb.ksrq\",\"xgsj\":\"0\"},"
			+ "{\"table\":\"jfjjzb\",\"xgsj\":\"0\"},{\"table\":\"tbjd\",\"xgsj\":\"0\"},{\"table\":\"gzjs\",\"xgsj\":\"0\"},{\"table\":\"qjfj\",\"xgsj\":\"0\"},"
			+ "{\"table\":\"yjdj\",\"xgsj\":\"0\"},{\"table\":\"sg\",\"xgsj\":\"0\"},{\"table\":\"em_zb\",\"xgsj\":\"0\"},{\"table\":\"em_qk\",\"join\":[\"em_zb\"],\"key\":"
			+ "\"em_qk.bh=em_zb.bh\",\"xgsj\":\"0\"},{\"table\":\"em_jd\",\"join\":[\"em_zb\"],\"key\":\"em_jd.bh=em_zb.bh\",\"xgsj\":\"0\"},{\"table\":\"em_jc\",\"join\":"
			+ "[\"em_zb\"],\"key\":\"em_jc.bh=em_zb.bh\",\"xgsj\":\"0\"},{\"table\":\"em_sy\",\"join\":[\"em_zb\"],\"key\":\"em_sy.bh=em_zb.bh\",\"xgsj\":\"0\"},"
			+ "{\"table\":\"fpa_zacy\",\"join\":[\"fpa_zb\",\"fpa_swry\"],\"key\":\"fpa_zacy.ah=fpa_zb.ah AND fpa_zacy.ah=fpa_swry.ah\",\"xgsj\":\"0\"},"
			+ "{\"table\":\"yma_zacy\",\"join\":[\"yma_zb\"],\"key\":\"yma_zacy.ah=yma_zb.ah\",\"xgsj\":\"0\"},{\"table\":\"wjp_bc\",\"join\":[\"wjp_zb\"],\"key\":"
			+ "\"wjp_bc.wjpid=wjp_zb.wjpid\",\"xgsj\":\"0\"},{\"table\":\"wyld_ry\",\"join\":[\"wyld_zb\"],\"key\":\"wyld_ry.wydid=wyld_zb.wydid\",\"xgsj\":\"0\"},"
			+ "{\"table\":\"hj\",\"join\":[\"hj_fb\"],\"key\":\"hj.hjid=hj_fb.hjid\",\"xgsj\":\"0\"},{\"table\":\"khjf\",\"xgsj\":\"0\"},{\"table\":\"khjf_sd\",\"xgsj\":\"0\"},"
			+ "{\"table\":\"khf\",\"xgsj\":\"0\"},{\"table\":\"thdj\",\"xgsj\":\"0\"},{\"table\":\"wp_bgzb\",\"join\":[\"wp_bgbc\"],\"key\":"
			+ "\"wp_bgzb.bh=wp_bgbc.bh AND wp_bgzb.djrq=wp_bgbc.djrq\",\"xgsj\":\"0\"},{\"table\":\"wwzk\",\"xgsj\":\"0\"},{\"table\":\"wwjc\",\"xgsj\":\"0\"},"
			+ "{\"table\":\"wwbx\",\"join\":[\"wwzk\"],\"key\":\"wwbx.bh=wwzk.bh AND wwbx.pzrq=wwzk.pzrq\",\"xgsj\":\"0\"},{\"table\":\"sndd\",\"xgsj\":\"0\"}]},"
			+ "{\"db\":\"MeetingDB\",\"rowversion\":\"starttime\",\"tables\":[{\"table\":\"pias/getItlist\",\"starttime\":\"" + fiveDayBefore + "\"}]},"
			+ "{\"db\":\"TalkDB\",\"rowversion\":\"starttime\",\"tables\":[{\"table\":\"qqdh/getTalklist\",\"starttime\":\"" + fiveDayBefore + "\"},{\"table\":\"qqdh/getQqdh\",\"starttime\":\"" + fiveDayBefore + "\"}]},"
			+ "{\"db\":\"PrasDB\",\"rowversion\":\"starttime\",\"tables\":[{\"table\":\"pras/getResult\",\"starttime\":\"" + fiveDayBefore + "\"},{\"table\":\"pras/getTable\",\"starttime\":\"" + fiveDayBefore + "\"}]},"
			+ "{\"db\":\"JfkhDB\",\"rowversion\":\"ID\",\"tables\":[{\"table\":\"BZ_JFKH_DRECORDSUB\",\"join\":[\"BZ_JFKH_DRECORD\"],\"key\":\"BZ_JFKH_DRECORDSUB.PID=BZ_JFKH_DRECORD.ID\",\"ID\":\"0\"},"
			+ "{\"table\":\"BZ_JFKH_MYZKJFSPSUB\",\"join\":[\"BZ_JFKH_MYZKJFSP\"],\"key\":\"BZ_JFKH_MYZKJFSPSUB.PID=BZ_JFKH_MYZKJFSP.ID\",\"ID\":\"0\"},{\"table\":\"BZ_JFKH_ZFFJQDDJL\",\"ID\":\"0\"},"
			+ "{\"table\":\"BZ_JFKH_ZFFYDDJL\",\"ID\":\"0\"},{\"table\":\"BZ_KHBZ_DOCTOR\",\"join_subtable\":[\"BZ_KHBZ_DOCTORSUB\"],\"key\":\"BZ_KHBZ_DOCTOR.ID=BZ_KHBZ_DOCTORSUB.PID\",\"ID\":\"0\"},"
			+ "{\"table\":\"BZ_KHBZ_JBSP\",\"ID\":\"0\"},{\"table\":\"BZ_KHBZ_JJJSP\",\"ID\":\"0\"},{\"table\":\"BZ_KHBZ_LJTQSP\",\"join_subtable\":[\"BZ_KHBZ_LJTQSPSUB\"],\"key\":\"BZ_KHBZ_LJTQSP.ID=BZ_KHBZ_LJTQSPSUB.PID\",\"ID\":\"0\"},"
			+ "{\"table\":\"BZ_KHBZ_TXLJTQSP\",\"ID\":\"0\"},{\"table\":\"BZ_KHBZ_XZCFSP\",\"ID\":\"0\"},{\"table\":\"BZ_KHBZ_XZJLSP\",\"ID\":\"0\"}]},"
			+ "{\"db\":\"DocumentDBForSqlite\",\"rowversion\":\"xgsj2\",\"tables\":"
			+ "[{\"table\":\"da_jbxx\",\"xgsj\":\"0\"},{\"table\":\"da_jl\",\"xgsj\":\"0\"},{\"table\":\"da_qklj\",\"xgsj\":\"0\"},{\"table\":\"da_shgx\",\"xgsj\":\"0\"},"
			+ "{\"table\":\"da_tzzb\",\"xgsj\":\"0\"},{\"table\":\"da_tszb\",\"join\":[\"da_tsbc\"],\"key\":\"da_tszb.bh=da_tsbc.bh\",\"xgsj\":\"0\"},"
			+ "{\"table\":\"bwxb\",\"xgsj\":\"0\"},{\"table\":\"tt\",\"xgsj\":\"0\"},{\"table\":\"lbc\",\"xgsj\":\"0\"},{\"table\":\"ss\",\"join\":[\"ssfb\"],\"key\":"
			+ "\"ss.ssid=ssfb.ssid\",\"xgsj\":\"0\"},{\"table\":\"ks\",\"join\":[\"ksfb\"],\"key\":\"ks.bh=ksfb.bh AND ks.ksrq=ksfb.ksrq\",\"xgsj\":\"0\"},"
			+ "{\"table\":\"jfjjzb\",\"xgsj\":\"0\"},{\"table\":\"tbjd\",\"xgsj\":\"0\"},{\"table\":\"gzjs\",\"xgsj\":\"0\"},{\"table\":\"qjfj\",\"xgsj\":\"0\"},"
			+ "{\"table\":\"yjdj\",\"xgsj\":\"0\"},{\"table\":\"sg\",\"xgsj\":\"0\"},{\"table\":\"em_zb\",\"xgsj\":\"0\"},{\"table\":\"em_qk\",\"join\":[\"em_zb\"],\"key\":"
			+ "\"em_qk.bh=em_zb.bh\",\"xgsj\":\"0\"},{\"table\":\"em_jd\",\"join\":[\"em_zb\"],\"key\":\"em_jd.bh=em_zb.bh\",\"xgsj\":\"0\"},{\"table\":\"em_jc\",\"join\":"
			+ "[\"em_zb\"],\"key\":\"em_jc.bh=em_zb.bh\",\"xgsj\":\"0\"},{\"table\":\"em_sy\",\"join\":[\"em_zb\"],\"key\":\"em_sy.bh=em_zb.bh\",\"xgsj\":\"0\"},"
			+ "{\"table\":\"fpa_zacy\",\"join\":[\"fpa_zb\",\"fpa_swry\"],\"key\":\"fpa_zacy.ah=fpa_zb.ah AND fpa_zacy.ah=fpa_swry.ah\",\"xgsj\":\"0\"},"
			+ "{\"table\":\"yma_zacy\",\"join\":[\"yma_zb\"],\"key\":\"yma_zacy.ah=yma_zb.ah\",\"xgsj\":\"0\"},{\"table\":\"wjp_bc\",\"join\":[\"wjp_zb\"],\"key\":"
			+ "\"wjp_bc.wjpid=wjp_zb.wjpid\",\"xgsj\":\"0\"},{\"table\":\"wyld_ry\",\"join\":[\"wyld_zb\"],\"key\":\"wyld_ry.wydid=wyld_zb.wydid\",\"xgsj\":\"0\"},"
			+ "{\"table\":\"hj\",\"join\":[\"hj_fb\"],\"key\":\"hj.hjid=hj_fb.hjid\",\"xgsj\":\"0\"},{\"table\":\"khjf\",\"xgsj\":\"0\"},{\"table\":\"khjf_sd\",\"xgsj\":\"0\"},"
			+ "{\"table\":\"khf\",\"xgsj\":\"0\"},{\"table\":\"thdj\",\"xgsj\":\"0\"},{\"table\":\"wp_bgzb\",\"join\":[\"wp_bgbc\"],\"key\":"
			+ "\"wp_bgzb.bh=wp_bgbc.bh AND wp_bgzb.djrq=wp_bgbc.djrq\",\"xgsj\":\"0\"},{\"table\":\"wwzk\",\"xgsj\":\"0\"},{\"table\":\"wwjc\",\"xgsj\":\"0\"},"
			+ "{\"table\":\"wwbx\",\"join\":[\"wwzk\"],\"key\":\"wwbx.bh=wwzk.bh AND wwbx.pzrq=wwzk.pzrq\",\"xgsj\":\"0\"},{\"table\":\"sndd\",\"xgsj\":\"0\"}]},"
			+ "{\"db\":\"VideoMeetingDB\",\"rowversion\":\"ID\",\"tables\":"
			+ "[{\"table\":\"MeetingApplies\",\"ID\":\"" + 0 + "\", \"join\":[\"UserAccounts\", \"Prisoner\", \"Users\", \"Departments\", \"Jails\"],"
			+ "\"key\":\" MeetingApplies.PrisonerFk = Prisoner.UserFk and MeetingApplies.CreateUserFk = UserAccounts.Id and Prisoner.UserFk = Users.Id and Users.DepartmentFk = Departments.Id and MeetingApplies.MeetingJailFk = Jails.Id\"}]},"
			+ "{\"db\":\"HelpDB\",\"rowversion\":\"ID\",\"tables\":["
			+ "{\"table\":\"Fee_UserCharges\",\"ID\":\"" + 0 + "\", \"join\":[\"Fee_UserAccounts\", \"Users\", \"Departments\", \"Prisoner\"],"
			+ "\"key\":\"  Fee_UserCharges.UserFk = Users.Id and Fee_UserCharges.UserAccountFk = Fee_UserAccounts.Id and Users.DepartmentFk = Departments.Id and Prisoner.UserFk = Users.Id\"},"
			+ "{\"table\":\"Fee_UserDeductions\",\"ID\":\"" + 0 + "\", \"join\":[\"Users\", \"Departments\", \"Prisoner\"],"
			+ "\"key\":\"Fee_UserDeductions.UserFk = Users.Id and Users.DepartmentFk = Departments.Id and Prisoner.UserFk = Users.Id\"},"
			+ "{\"table\":\"Contacts\",\"ID\":\"" + 0 + "\", \"join\":[\"Prisoner\", \"Users\", \"Departments\", \"CommonCodes\"],"
			+ "\"key\":\"Contacts.PrisonerFk = Prisoner.UserFk and Prisoner.UserFk = Users.Id and Contacts.RelationFk = CommonCodes.Id and Users.DepartmentFk = Departments.Id\"},"
			+ "{\"table\":\"Sms_SmsSendBoxes\",\"ID\":\"" + 0 + "\", \"join\":[\"Prisoner\", \"Users\", \"Departments\", \"CommonCodes\"],"
			+ "\"key\":\"Sms_SmsSendBoxes.PrisonerFk = Prisoner.UserFk and Prisoner.UserFk = Users.Id and Sms_SmsSendBoxes.FailureReasonFk = CommonCodes.Id and Sms_SmsSendBoxes.FeelingFk = CommonCodes.Id "
			+ "and Sms_SmsSendBoxes.RelationFk = CommonCodes.Id and Users.DepartmentFk = Departments.Id\"},"
			+ "{\"table\":\"Sms_SmsReceiveBoxes\",\"ID\":\"" + 0 + "\", \"join\":[\"Prisoner\", \"Users\", \"Departments\", \"CommonCodes\"],"
			+ "\"key\":\"Sms_SmsReceiveBoxes.PrisonerFk = Prisoner.UserFk and Prisoner.UserFk = Users.Id and Sms_SmsReceiveBoxes.FailureReasonFk = CommonCodes.Id and Sms_SmsReceiveBoxes.RelationFk = CommonCodes.Id "
			+ "and Users.DepartmentFk = Departments.Id\"},"
			+ "{\"table\":\"ClassRoom_Histories\",\"ID\":\"" + 0 + "\", \"join\":[\"ClassRoom_Videos\", \"Users\", \"Departments\", \"CommonCodes\", \"Prisoner\"],"
			+ "\"key\":\"ClassRoom_Histories.UserFk = Users.Id and ClassRoom_Histories.VideoFk = ClassRoom_Videos.Id and ClassRoom_Videos.TypeFk = CommonCodes.Id and Users.DepartmentFk = Departments.Id and Prisoner.UserFk = Users.Id\"},"
			+ "{\"table\":\"ClassRoom_StudyNotes\",\"ID\":\"" + 0 + "\", \"join\":[\"ClassRoom_Videos\", \"Users\", \"Departments\", \"CommonCodes\", \"Prisoner\"],"
			+ "\"key\":\"ClassRoom_StudyNotes.UserFk = Users.Id and ClassRoom_StudyNotes.VideoFk = ClassRoom_Videos.Id and ClassRoom_Videos.TypeFk = CommonCodes.Id and Users.DepartmentFk = Departments.Id and Prisoner.UserFk = Users.Id\"},"
			+ "{\"table\":\"Library_BookHistories\",\"ID\":\"" + 0 + "\", \"join\":[\"Library_Books\", \"Users\", \"Departments\", \"CommonCodes\", \"Prisoner\"],"
			+ "\"key\":\"Library_BookHistories.UserFk = Users.Id and Library_BookHistories.BookFk = Library_Books.Id and Library_Books.CatalogFk = CommonCodes.Id and Users.DepartmentFk = Departments.Id and Prisoner.UserFk = Users.Id\"},"
			+ "{\"table\":\"Library_BookReviews\",\"ID\":\"" + 0 + "\", \"join\":[\"Library_Books\", \"Users\", \"Departments\", \"CommonCodes\", \"Prisoner\"],"
			+ "\"key\":\"Library_BookReviews.UserFk = Users.Id and Library_BookReviews.BookFk = Library_Books.Id and Library_Books.CatalogFk = CommonCodes.Id and Users.DepartmentFk = Departments.Id and Prisoner.UserFk = Users.Id\"},"
			+ "{\"table\":\"Vod_VideoHistories\",\"ID\":\"" + 0 + "\", \"join\":[\"Vod_Videos\", \"Users\", \"Departments\", \"CommonCodes\", \"Prisoner\"],"
			+ "\"key\":\"Vod_VideoHistories.UserFk = Users.Id and Vod_VideoHistories.VideoFk = Vod_Videos.Id and Vod_Videos.CatalogFk = CommonCodes.Id and Users.DepartmentFk = Departments.Id and Prisoner.UserFk = Users.Id\"},"
			+ "{\"table\":\"Vod_VideoReviews\",\"ID\":\"" + 0 + "\", \"join\":[\"Vod_Videos\", \"Users\", \"Departments\", \"CommonCodes\", \"Prisoner\"],"
			+ "\"key\":\"Vod_VideoReviews.UserFk = Users.Id and Vod_VideoReviews.VideoFk = Vod_Videos.Id and Vod_Videos.CatalogFk = CommonCodes.Id and Users.DepartmentFk = Departments.Id and Prisoner.UserFk = Users.Id\"},"
			+ "{\"table\":\"Terminal_TerminalApplications\",\"ID\":\"" + 0 + "\", \"join_sub\":[\"Terminal_SubscribeInfos\", \"TerminalApplications_SubscribeInfos\", \"Terminal_TerminalInfos\", \"Terminal_TerminalModels\", \"Users\", \"Departments\", \"Prisoner\"],"
			+ "\"key\":\"Terminal_TerminalApplications.UserFk = Users.Id and Terminal_TerminalApplications.TerminalFk = Terminal_TerminalInfos.Id "
			+ "and Terminal_TerminalApplications.TerminalModelFk = Terminal_TerminalModels.Id and TerminalApplications_SubscribeInfos.SubscribeInfoFk = Terminal_SubscribeInfos.Id "
			+ "and TerminalApplications_SubscribeInfos.TerminalApplicationFk = Terminal_TerminalApplications.Id and Users.DepartmentFk = Departments.Id and Prisoner.UserFk = Users.Id\"},"
			+ "{\"table\":\"Messages\",\"ID\":\"" + 0 + "\", \"join\":[\"Users\", \"Departments\", \"Prisoner\"],\"key\":\"Messages.UserFk = Users.Id and Users.DepartmentFk = Departments.Id and Prisoner.UserFk = Users.Id\"},"
			+ "{\"table\":\"RainGlass_UserEmotions\",\"ID\":\"" + 0 + "\", \"join\":[\"RainGlass_Emotions\", \"RainGlass_EmotionPersuasions\", \"Users\", \"Departments\", \"Prisoner\"],"
			+ "\"key\":\"RainGlass_UserEmotions.UserFk = Users.Id and RainGlass_UserEmotions.EmotionFk = RainGlass_Emotions.Id and RainGlass_Emotions.Id = RainGlass_EmotionPersuasions.EmotionFk and Users.DepartmentFk = Departments.Id and Prisoner.UserFk = Users.Id\"},"
			+ "{\"table\":\"MusicAppre_PlayHistories\",\"ID\":\"" + 0 + "\", \"join\":[\"MusicAppre_Musics\", \"Users\", \"Departments\", \"CommonCodes\", \"Prisoner\"],"
			+ "\"key\":\"MusicAppre_PlayHistories.MusicFk = MusicAppre_Musics.Id and MusicAppre_PlayHistories.UserFk = Users.Id "
			+ "and Users.DepartmentFk = Departments.Id and MusicAppre_Musics.CatalogFk = CommonCodes.Id and Prisoner.UserFk = Users.Id\"}]}]}";

	public static String AllDBInitJsonForMock = "{\"databases\":[{\"db\":\"DocumentDB\",\"rowversion\":\"xgsj\",\"tables\":"
			+ "[{\"table\":\"da_jbxx\",\"xgsj\":\"0\"}]},"
			+ "{\"db\":\"MeetingDB\",\"rowversion\":\"starttime\",\"tables\":[{\"table\":\"pias/getItlist\",\"starttime\":\"" + fiveDayBefore + "\"}]},"
			+ "{\"db\":\"TalkDB\",\"rowversion\":\"starttime\",\"tables\":[{\"table\":\"qqdh/getTalklist\",\"starttime\":\"" + fiveDayBefore + "\"}]},"
			+ "{\"db\":\"PrasDB\",\"rowversion\":\"starttime\",\"tables\":[{\"table\":\"pras/getResult\",\"starttime\":\"" + fiveDayBefore + "\"}]},"
			+ "{\"db\":\"JfkhDB\",\"rowversion\":\"ID\",\"tables\":[{\"table\":\"BZ_JFKH_DRECORDSUB\",\"join\":[\"BZ_JFKH_DRECORD\"],\"key\":\"BZ_JFKH_DRECORDSUB.PID=BZ_JFKH_DRECORD.ID\",\"ID\":\"0\"}]},"
			+ "{\"db\":\"VideoMeetingDB\",\"rowversion\":\"ID\",\"tables\":"
			+ "[{\"table\":\"MeetingApplies\",\"ID\":\"" + 0 + "\", \"join\":[\"UserAccounts\", \"Prisoner\", \"Users\", \"Departments\", \"Jails\"],"
			+ "\"key\":\" MeetingApplies.PrisonerFk = Prisoner.UserFk and MeetingApplies.CreateUserFk = UserAccounts.Id and Prisoner.UserFk = Users.Id and Users.DepartmentFk = Departments.Id and MeetingApplies.MeetingJailFk = Jails.Id\"}]},"
			+ "{\"db\":\"HelpDB\",\"rowversion\":\"ID\",\"tables\":"
			+ "[{\"table\":\"Fee_UserCharges\",\"ID\":\"" + 0 + "\", \"join\":[\"Fee_UserAccounts\", \"Users\", \"Departments\"],"
			+ "\"key\":\"Fee_UserCharges.UserFk = Users.Id and Fee_UserCharges.UserAccountFk = Fee_UserAccounts.Id and Users.DepartmentFk = Departments.Id\"}]}]}";

//	public static String AllDBInitJsonForMockTest = "";
	public static String AllDBInitJsonForMockTest = 
			"{\"databases\":["
//			+ "{\"databaseName\":\"DocumentDB\",\"databaseType\":\"sqlserver\",\"tables\":["
//			+ "{\"tableName\":\"da_jbxx\",\"versionColumn\":\"xgsj\",\"versionOffset\":\"0\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\",\"versionStep\":\"100\"},"
//			+ "{\"tableName\":\"da_jl\",\"versionColumn\":\"xgsj\",\"versionOffset\":\"0\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\",\"versionStep\":\"100\"},"
//			+ "{\"tableName\":\"da_qklj\",\"versionColumn\":\"xgsj\",\"versionOffset\":\"0\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\",\"versionStep\":\"100\"},"
//			+ "{\"tableName\":\"da_shgx\",\"versionColumn\":\"xgsj\",\"versionOffset\":\"0\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\",\"versionStep\":\"100\"},"
//			+ "{\"tableName\":\"da_tzzb\",\"versionColumn\":\"xgsj\",\"versionOffset\":\"0\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\",\"versionStep\":\"100\"},"
//			+ "{\"tableName\":\"da_tszb\",\"versionColumn\":\"xgsj\",\"versionOffset\":\"0\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\",\"versionStep\":\"100\",\"join\":[\"da_tsbc\"],\"key\":\"da_tszb.bh=da_tsbc.bh\"},"
//			+ "{\"tableName\":\"bwxb\",\"versionColumn\":\"xgsj\",\"versionOffset\":\"0\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\",\"versionStep\":\"100\"},"
//			+ "{\"tableName\":\"tt\",\"versionColumn\":\"xgsj\",\"versionOffset\":\"0\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\",\"versionStep\":\"100\"},"
//			+ "{\"tableName\":\"lbc\",\"versionColumn\":\"xgsj\",\"versionOffset\":\"0\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\",\"versionStep\":\"100\"},"
//			+ "{\"tableName\":\"ss\",\"versionColumn\":\"xgsj\",\"versionOffset\":\"0\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\",\"versionStep\":\"100\",\"join\":[\"ssfb\"],\"key\":\"ss.ssid=ssfb.ssid\"},"
//			+ "{\"tableName\":\"ks\",\"versionColumn\":\"xgsj\",\"versionOffset\":\"0\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\",\"versionStep\":\"100\",\"join\":[\"ksfb\"],\"key\":\"ks.bh=ksfb.bh AND ks.ksrq=ksfb.ksrq\"},"
//			+ "{\"tableName\":\"jfjjzb\",\"versionColumn\":\"xgsj\",\"versionOffset\":\"0\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\",\"versionStep\":\"100\"},"
//			+ "{\"tableName\":\"tbjd\",\"versionColumn\":\"xgsj\",\"versionOffset\":\"0\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\",\"versionStep\":\"100\"},"
//			+ "{\"tableName\":\"gzjs\",\"versionColumn\":\"xgsj\",\"versionOffset\":\"0\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\",\"versionStep\":\"100\"},"
//			+ "{\"tableName\":\"qjfj\",\"versionColumn\":\"xgsj\",\"versionOffset\":\"0\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\",\"versionStep\":\"100\"},"
//			+ "{\"tableName\":\"yjdj\",\"versionColumn\":\"xgsj\",\"versionOffset\":\"0\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\",\"versionStep\":\"100\"},"
//			+ "{\"tableName\":\"sg\",\"versionColumn\":\"xgsj\",\"versionOffset\":\"0\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\",\"versionStep\":\"100\"},"
//			+ "{\"tableName\":\"em_zb\",\"versionColumn\":\"xgsj\",\"versionOffset\":\"0\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\",\"versionStep\":\"100\"},"
//			+ "{\"tableName\":\"em_qk\",\"versionColumn\":\"xgsj\",\"versionOffset\":\"0\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\",\"versionStep\":\"100\",\"join\":[\"em_zb\"],\"key\":\"em_qk.bh=em_zb.bh\"},"
//			+ "{\"tableName\":\"em_jd\",\"versionColumn\":\"xgsj\",\"versionOffset\":\"0\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\",\"versionStep\":\"100\",\"join\":[\"em_zb\"],\"key\":\"em_jd.bh=em_zb.bh\"},"
//			+ "{\"tableName\":\"em_jc\",\"versionColumn\":\"xgsj\",\"versionOffset\":\"0\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\",\"versionStep\":\"100\",\"join\":[\"em_zb\"],\"key\":\"em_jc.bh=em_zb.bh\"},"
//			+ "{\"tableName\":\"em_sy\",\"versionColumn\":\"xgsj\",\"versionOffset\":\"0\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\",\"versionStep\":\"100\",\"join\":[\"em_zb\"],\"key\":\"em_sy.bh=em_zb.bh\"},"
//			+ "{\"tableName\":\"fpa_zacy\",\"versionColumn\":\"xgsj\",\"versionOffset\":\"0\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\",\"versionStep\":\"100\",\"join\":[\"fpa_zb\",\"fpa_swry\"],\"key\":\"fpa_zacy.ah=fpa_zb.ah AND fpa_zacy.ah=fpa_swry.ah\"},"
//			+ "{\"tableName\":\"yma_zacy\",\"versionColumn\":\"xgsj\",\"versionOffset\":\"0\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\",\"versionStep\":\"100\",\"join\":[\"yma_zb\"],\"key\":\"yma_zacy.ah=yma_zb.ah\"},"
//			+ "{\"tableName\":\"wjp_bc\",\"versionColumn\":\"xgsj\",\"versionOffset\":\"0\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\",\"versionStep\":\"100\",\"join\":[\"wjp_zb\"],\"key\":\"wjp_bc.wjpid=wjp_zb.wjpid\"},"
//			+ "{\"tableName\":\"wyld_ry\",\"versionColumn\":\"xgsj\",\"versionOffset\":\"0\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\",\"versionStep\":\"100\",\"join\":[\"wyld_zb\"],\"key\":\"wyld_ry.wydid=wyld_zb.wydid\"},"
//			+ "{\"tableName\":\"hj\",\"versionColumn\":\"xgsj\",\"versionOffset\":\"0\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\",\"versionStep\":\"100\",\"join\":[\"hj_fb\"],\"key\":\"hj.hjid=hj_fb.hjid\"},"
//			+ "{\"tableName\":\"khjf\",\"versionColumn\":\"xgsj\",\"versionOffset\":\"0\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\",\"versionStep\":\"100\"},"
//			+ "{\"tableName\":\"khjf_sd\",\"versionColumn\":\"xgsj\",\"versionOffset\":\"0\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\",\"versionStep\":\"100\"},"
//			+ "{\"tableName\":\"khf\",\"versionColumn\":\"xgsj\",\"versionOffset\":\"0\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\",\"versionStep\":\"100\"},"
//			+ "{\"tableName\":\"thdj\",\"versionColumn\":\"xgsj\",\"versionOffset\":\"0\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\",\"versionStep\":\"100\"},"
//			+ "{\"tableName\":\"wp_bgzb\",\"versionColumn\":\"xgsj\",\"versionOffset\":\"0\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\",\"versionStep\":\"100\",\"join\":[\"wp_bgbc\"],\"key\":\"wp_bgzb.bh=wp_bgbc.bh AND wp_bgzb.djrq=wp_bgbc.djrq\"},"
//			+ "{\"tableName\":\"wwzk\",\"versionColumn\":\"xgsj\",\"versionOffset\":\"0\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\",\"versionStep\":\"100\"},"
//			+ "{\"tableName\":\"wwjc\",\"versionColumn\":\"xgsj\",\"versionOffset\":\"0\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\",\"versionStep\":\"100\"},"
//			+ "{\"tableName\":\"wwbx\",\"versionColumn\":\"xgsj\",\"versionOffset\":\"0\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\",\"versionStep\":\"100\",\"join\":[\"wwzk\"],\"key\":\"wwbx.bh=wwzk.bh AND wwbx.pzrq=wwzk.pzrq\"},"
//			+ "{\"tableName\":\"sndd\",\"versionColumn\":\"xgsj\",\"versionOffset\":\"0\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\",\"versionStep\":\"100\"}]},"
//			+ "{\"databaseName\":\"MeetingDB\",\"databaseType\":\"webservice\",\"tables\":["
//			+ "{\"tableName\":\"pias/getItlist\",\"versionColumn\":\"starttime\",\"versionOffset\":\"1478361600000\",\"versionStep\":\"86400000\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\"}]},"
//			+ "{\"databaseName\":\"TalkDB\",\"databaseType\":\"webservice\",\"tables\":["
//			+ "{\"tableName\":\"qqdh/getTalklist\",\"versionColumn\":\"starttime\",\"versionOffset\":\"1478361600000\",\"versionStep\":\"86400000\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\"},"
//			+ "{\"tableName\":\"qqdh/getQqdh\",\"versionColumn\":\"starttime\",\"versionOffset\":\"1478361600000\",\"versionStep\":\"86400000\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\"}]},"
//			+ "{\"databaseName\":\"PrasDB\",\"databaseType\":\"webservice\",\"tables\":["
//			+ "{\"tableName\":\"pras/getResult\",\"versionColumn\":\"starttime\",\"versionOffset\":\"1478361600000\",\"versionStep\":\"86400000\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\"},"
//			+ "{\"tableName\":\"pras/getTable\",\"versionColumn\":\"starttime\",\"versionOffset\":\"1478361600000\",\"versionStep\":\"86400000\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\"}]},"
//			+ "{\"databaseName\":\"JfkhDB\",\"databaseType\":\"oracle\",\"tables\":["
//			+ "{\"tableName\":\"BZ_JFKH_DRECORDSUB\",\"join\":[\"BZ_JFKH_DRECORD\"],\"key\":\"BZ_JFKH_DRECORDSUB.PID=BZ_JFKH_DRECORD.ID\",\"versionColumn\":\"ID\",\"versionOffset\":\"0\",\"versionStep\":\"100\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\"},"
//			+ "{\"tableName\":\"BZ_JFKH_MYZKJFSPSUB\",\"join\":[\"BZ_JFKH_MYZKJFSP\"],\"key\":\"BZ_JFKH_MYZKJFSPSUB.PID=BZ_JFKH_MYZKJFSP.ID\",\"versionColumn\":\"ID\",\"versionOffset\":\"0\",\"versionStep\":\"100\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\"},"
//			+ "{\"tableName\":\"BZ_JFKH_ZFFJQDDJL\",\"versionColumn\":\"ID\",\"versionOffset\":\"0\",\"versionStep\":\"100\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\"},"
//			+ "{\"tableName\":\"BZ_JFKH_ZFFYDDJL\",\"versionColumn\":\"ID\",\"versionOffset\":\"0\",\"versionStep\":\"100\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\"},"
//			+ "{\"tableName\":\"BZ_KHBZ_DOCTOR\",\"join_subtable\":[\"BZ_KHBZ_DOCTORSUB\"],\"key\":\"BZ_KHBZ_DOCTOR.ID=BZ_KHBZ_DOCTORSUB.PID\",\"versionColumn\":\"ID\",\"versionOffset\":\"0\",\"versionStep\":\"100\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\"},"
//			+ "{\"tableName\":\"BZ_KHBZ_JBSP\",\"versionColumn\":\"ID\",\"versionOffset\":\"0\",\"versionStep\":\"100\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\"},"
//			+ "{\"tableName\":\"BZ_KHBZ_JJJSP\",\"versionColumn\":\"ID\",\"versionOffset\":\"0\",\"versionStep\":\"100\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\"},"
//			+ "{\"tableName\":\"BZ_KHBZ_LJTQSP\",\"join_subtable\":[\"BZ_KHBZ_LJTQSPSUB\"],\"key\":\"BZ_KHBZ_LJTQSP.ID=BZ_KHBZ_LJTQSPSUB.PID\",\"versionColumn\":\"ID\",\"versionOffset\":\"0\",\"versionStep\":\"100\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\"},"
//			+ "{\"tableName\":\"BZ_KHBZ_TXLJTQSP\",\"versionColumn\":\"ID\",\"versionOffset\":\"0\",\"versionStep\":\"100\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\"},"
//			+ "{\"tableName\":\"BZ_KHBZ_XZCFSP\",\"versionColumn\":\"ID\",\"versionOffset\":\"0\",\"versionStep\":\"100\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\"},"
//			+ "{\"tableName\":\"BZ_KHBZ_XZJLSP\",\"versionColumn\":\"ID\",\"versionOffset\":\"0\",\"versionStep\":\"100\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\"}]},"
//			+ "{\"databaseName\":\"HelpDB\",\"databaseType\":\"sqlserver\",\"tables\":["
//			+ "{\"tableName\":\"Fee_UserCharges\",\"join\":[\"Fee_UserAccounts\", \"Users\", \"Departments\", \"Prisoner\"],\"key\":\"Fee_UserCharges.UserFk = Users.Id and Fee_UserCharges.UserAccountFk = Fee_UserAccounts.Id and Users.DepartmentFk = Departments.Id and Prisoner.UserFk = Users.Id\",\"versionColumn\":\"ID\",\"versionOffset\":\"0\",\"versionStep\":\"100\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\"},"
//			+ "{\"tableName\":\"Fee_UserDeductions\",\"join\":[\"Users\", \"Departments\", \"Prisoner\"],\"key\":\"Fee_UserDeductions.UserFk = Users.Id and Users.DepartmentFk = Departments.Id and Prisoner.UserFk = Users.Id\",\"versionColumn\":\"ID\",\"versionOffset\":\"0\",\"versionStep\":\"100\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\"},"
//			+ "{\"tableName\":\"Contacts\",\"join\":[\"Prisoner\", \"Users\", \"Departments\", \"CommonCodes\"],\"key\":\"Contacts.PrisonerFk = Prisoner.UserFk and Prisoner.UserFk = Users.Id and Contacts.RelationFk = CommonCodes.Id and Users.DepartmentFk = Departments.Id\",\"versionColumn\":\"ID\",\"versionOffset\":\"0\",\"versionStep\":\"100\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\"},"
//			+ "{\"tableName\":\"Sms_SmsSendBoxes\",\"join\":[\"Prisoner\", \"Users\", \"Departments\", \"CommonCodes\"],\"key\":\"Sms_SmsSendBoxes.PrisonerFk = Prisoner.UserFk and Prisoner.UserFk = Users.Id and Sms_SmsSendBoxes.FailureReasonFk = CommonCodes.Id and Sms_SmsSendBoxes.FeelingFk = CommonCodes.Id and Sms_SmsSendBoxes.RelationFk = CommonCodes.Id and Users.DepartmentFk = Departments.Id\",\"versionColumn\":\"ID\",\"versionOffset\":\"0\",\"versionStep\":\"100\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\"},"
//			+ "{\"tableName\":\"Sms_SmsReceiveBoxes\",\"join\":[\"Prisoner\", \"Users\", \"Departments\", \"CommonCodes\"],\"key\":\"Sms_SmsReceiveBoxes.PrisonerFk = Prisoner.UserFk and Prisoner.UserFk = Users.Id and Sms_SmsReceiveBoxes.FailureReasonFk = CommonCodes.Id and Sms_SmsReceiveBoxes.RelationFk = CommonCodes.Id and Users.DepartmentFk = Departments.Id\",\"versionColumn\":\"ID\",\"versionOffset\":\"0\",\"versionStep\":\"100\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\"},"
//			+ "{\"tableName\":\"ClassRoom_Histories\",\"join\":[\"ClassRoom_Videos\", \"Users\", \"Departments\", \"CommonCodes\", \"Prisoner\"],\"key\":\"ClassRoom_Histories.UserFk = Users.Id and ClassRoom_Histories.VideoFk = ClassRoom_Videos.Id and ClassRoom_Videos.TypeFk = CommonCodes.Id and Users.DepartmentFk = Departments.Id and Prisoner.UserFk = Users.Id\",\"versionColumn\":\"ID\",\"versionOffset\":\"0\",\"versionStep\":\"100\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\"},"
//			+ "{\"tableName\":\"ClassRoom_StudyNotes\",\"join\":[\"ClassRoom_Videos\", \"Users\", \"Departments\", \"CommonCodes\", \"Prisoner\"],\"key\":\"ClassRoom_StudyNotes.UserFk = Users.Id and ClassRoom_StudyNotes.VideoFk = ClassRoom_Videos.Id and ClassRoom_Videos.TypeFk = CommonCodes.Id and Users.DepartmentFk = Departments.Id and Prisoner.UserFk = Users.Id\",\"versionColumn\":\"ID\",\"versionOffset\":\"0\",\"versionStep\":\"100\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\"},"
//			+ "{\"tableName\":\"Library_BookHistories\",\"join\":[\"Library_Books\", \"Users\", \"Departments\", \"CommonCodes\", \"Prisoner\"],\"key\":\"Library_BookHistories.UserFk = Users.Id and Library_BookHistories.BookFk = Library_Books.Id and Library_Books.CatalogFk = CommonCodes.Id and Users.DepartmentFk = Departments.Id and Prisoner.UserFk = Users.Id\",\"versionColumn\":\"ID\",\"versionOffset\":\"0\",\"versionStep\":\"100\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\"},"
//			+ "{\"tableName\":\"Library_BookReviews\",\"join\":[\"Library_Books\", \"Users\", \"Departments\", \"CommonCodes\", \"Prisoner\"],\"key\":\"Library_BookReviews.UserFk = Users.Id and Library_BookReviews.BookFk = Library_Books.Id and Library_Books.CatalogFk = CommonCodes.Id and Users.DepartmentFk = Departments.Id and Prisoner.UserFk = Users.Id\",\"versionColumn\":\"ID\",\"versionOffset\":\"0\",\"versionStep\":\"100\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\"},"
//			+ "{\"tableName\":\"Vod_VideoHistories\",\"join\":[\"Vod_Videos\", \"Users\", \"Departments\", \"CommonCodes\", \"Prisoner\"],\"key\":\"Vod_VideoHistories.UserFk = Users.Id and Vod_VideoHistories.VideoFk = Vod_Videos.Id and Vod_Videos.CatalogFk = CommonCodes.Id and Users.DepartmentFk = Departments.Id and Prisoner.UserFk = Users.Id\",\"versionColumn\":\"ID\",\"versionOffset\":\"0\",\"versionStep\":\"100\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\"},"
//			+ "{\"tableName\":\"Vod_VideoReviews\",\"join\":[\"Vod_Videos\", \"Users\", \"Departments\", \"CommonCodes\", \"Prisoner\"],\"key\":\"Vod_VideoReviews.UserFk = Users.Id and Vod_VideoReviews.VideoFk = Vod_Videos.Id and Vod_Videos.CatalogFk = CommonCodes.Id and Users.DepartmentFk = Departments.Id and Prisoner.UserFk = Users.Id\",\"versionColumn\":\"ID\",\"versionOffset\":\"0\",\"versionStep\":\"100\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\"},"
//			+ "{\"tableName\":\"Terminal_TerminalApplications\",\"join_subtable\":[\"Terminal_SubscribeInfos\", \"TerminalApplications_SubscribeInfos\", \"Terminal_TerminalInfos\", \"Terminal_TerminalModels\", \"Users\", \"Departments\", \"Prisoner\"],\"key\":\"Terminal_TerminalApplications.UserFk = Users.Id and Terminal_TerminalApplications.TerminalFk = Terminal_TerminalInfos.Id and Terminal_TerminalApplications.TerminalModelFk = Terminal_TerminalModels.Id and TerminalApplications_SubscribeInfos.SubscribeInfoFk = Terminal_SubscribeInfos.Id and TerminalApplications_SubscribeInfos.TerminalApplicationFk = Terminal_TerminalApplications.Id and Users.DepartmentFk = Departments.Id and Prisoner.UserFk = Users.Id\",\"versionColumn\":\"ID\",\"versionOffset\":\"0\",\"versionStep\":\"100\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\"},"
//			+ "{\"tableName\":\"Messages\",\"join\":[\"Users\", \"Departments\", \"Prisoner\"],\"key\":\"Messages.UserFk = Users.Id and Users.DepartmentFk = Departments.Id and Prisoner.UserFk = Users.Id\",\"versionColumn\":\"ID\",\"versionOffset\":\"0\",\"versionStep\":\"100\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\"},"
//			+ "{\"tableName\":\"RainGlass_UserEmotions\",\"join\":[\"RainGlass_Emotions\", \"RainGlass_EmotionPersuasions\", \"Users\", \"Departments\", \"Prisoner\"],\"key\":\"RainGlass_UserEmotions.UserFk = Users.Id and RainGlass_UserEmotions.EmotionFk = RainGlass_Emotions.Id and RainGlass_Emotions.Id = RainGlass_EmotionPersuasions.EmotionFk and Users.DepartmentFk = Departments.Id and Prisoner.UserFk = Users.Id\",\"versionColumn\":\"ID\",\"versionOffset\":\"0\",\"versionStep\":\"100\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\"},"
//			+ "{\"tableName\":\"MusicAppre_PlayHistories\",\"join\":[\"MusicAppre_Musics\", \"Users\", \"Departments\", \"CommonCodes\", \"Prisoner\"],\"key\":\"MusicAppre_PlayHistories.MusicFk = MusicAppre_Musics.Id and MusicAppre_PlayHistories.UserFk = Users.Id and Users.DepartmentFk = Departments.Id and MusicAppre_Musics.CatalogFk = CommonCodes.Id and Prisoner.UserFk = Users.Id\",\"versionColumn\":\"ID\",\"versionOffset\":\"0\",\"versionStep\":\"100\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\"}]},"
//			+ "{\"databaseName\":\"VideoMeetingDB\",\"databaseType\":\"sqlserver\",\"tables\":["
//			+ "{\"tableName\":\"MeetingApplies\",\"join\":[\"UserAccounts\", \"Prisoner\", \"Users\", \"Departments\", \"Jails\"],\"key\":\"MeetingApplies.PrisonerFk = Prisoner.UserFk and MeetingApplies.CreateUserFk = UserAccounts.Id and Prisoner.UserFk = Users.Id and Users.DepartmentFk = Departments.Id and MeetingApplies.MeetingJailFk = Jails.Id\",\"versionColumn\":\"ID\",\"versionOffset\":\"0\",\"versionStep\":\"100\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\"}]},"
//			+ "{\"databaseName\":\"DocumentFiles\",\"databaseType\":\"smallFiles\",\"tables\":["
//			+ "{\"tableName\":\"/home/beaver/Documents/test/test1\",\"versionColumn\":\"modifyTime\",\"versionOffset\":\"0\",\"versionStep\":\"86400000\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\"}]},"
//			+ "{\"databaseName\":\"XfzxDB\",\"databaseType\":\"oracle\",\"tables\":["
//			+ "{\"tableName\":\"TBFLOW\",\"join\":[\"TBFLOW_BASE\",\"TBFLOW_CASE_CRIMINAL\",\"TBFLOW_OTHER_FLOW\",\"TBFLOW_BASE_OTHER\"],\"key\":\"TBFLOW.FLOWDRAFTID=TBFLOW_BASE.FLOWDRAFTID AND TBFLOW_BASE.FLOWDEFID=TBFLOW_CASE_CRIMINAL.FLOWDEFID AND TBFLOW.FLOWDRAFTID=TBFLOW_OTHER_FLOW.FLOWDRAFTID AND TBFLOW_OTHER_FLOW.OTHERID=TBFLOW_BASE_OTHER.OTHERID\",\"versionColumn\":\"FLOWSN\",\"versionOffset\":\"0\",\"versionStep\":\"100\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\"},"
//			+ "{\"tableName\":\"TBPRISONER_MEETING_SUMMARY\",\"join\":[\"TBPRISONER_MEETING_CRIMINAL\"],\"key\":\"TBPRISONER_MEETING_CRIMINAL.MKEY=TBPRISONER_MEETING_SUMMARY.MKEY\",\"versionColumn\":\"MDATE\",\"versionColumnType\":\"DATETIME\",\"versionOffset\":\"20160715000000\",\"versionStep\":\"600\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\"},"
//			+ "{\"tableName\":\"TBXF_SENTENCEALTERATION\",\"versionColumn\":\"OPTIME\",\"versionColumnType\":\"DATETIME\",\"versionOffset\":\"20160715000000\",\"versionStep\":\"3600\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\"},"
//			+ "{\"tableName\":\"TBXF_SCREENING\",\"versionColumn\":\"OPTIME\",\"versionColumnType\":\"DATETIME\",\"versionOffset\":\"20160715040000\",\"versionStep\":\"3600\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\"},"
//			+ "{\"tableName\":\"TBXF_PRISONERPERFORMANCE\",\"versionColumn\":\"OPTIME\",\"versionColumnType\":\"DATETIME\",\"versionOffset\":\"20160715000000\",\"versionStep\":\"600\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\"}]},"
			+ "{\"databaseName\":\"MysqlTest\",\"databaseType\":\"mysql\",\"tables\":["
			+ "{\"tableName\":\"users\",\"versionColumn\":\"ID\",\"minVersion\":\"0\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\"},"
			+ "{\"tableName\":\"student\",\"join\":[\"department\"],\"key\":\"student.depId=department.ID\",\"versionColumn\":\"ID\",\"minVersion\":\"0\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\"},"
			+ "{\"tableName\":\"course\",\"versionColumn\":\"TOTAL_LINES_NUMBER\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\"},"
			+ "{\"tableName\":\"teacher\",\"join\":[\"department\"],\"key\":\"teacher.depId=department.ID\",\"versionColumn\":\"TOTAL_LINES_NUMBER\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\"},"
			+ "{\"tableName\":\"date_test\",\"join\":[\"department\"],\"key\":\"date_test.depId=department.ID\",\"versionColumn\":\"ID\",\"minVersion\":\"0\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\"},"
			+ "{\"tableName\":\"datetime_test\",\"join\":[\"department\"],\"key\":\"datetime_test.depId=department.ID\",\"versionColumn\":\"ID\",\"minVersion\":\"0\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\"},"
			+ "{\"tableName\":\"timestamp_test\",\"join\":[\"department\"],\"key\":\"timestamp_test.depId=department.ID\",\"versionColumn\":\"ID\",\"minVersion\":\"0\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\"}"
			+ "]},"
			+ "{\"databaseName\":\"SqlServerTest\",\"databaseType\":\"sqlserver\",\"tables\":["
			+ "{\"tableName\":\"users\",\"versionColumn\":\"ID\",\"minVersion\":\"0\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\"},"
			+ "{\"tableName\":\"student\",\"join\":[\"department\"],\"key\":\"student.depId=department.ID\",\"versionColumn\":\"ID\",\"minVersion\":\"0\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\"},"
			+ "{\"tableName\":\"course\",\"versionColumn\":\"TOTAL_LINES_NUMBER\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\"},"
			+ "{\"tableName\":\"teacher\",\"join\":[\"department\"],\"key\":\"teacher.depId=department.ID\",\"versionColumn\":\"TOTAL_LINES_NUMBER\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\"},"
			+ "{\"tableName\":\"datetime_test\",\"join\":[\"department\"],\"key\":\"datetime_test.depId=department.ID\",\"versionColumn\":\"ID\",\"minVersion\":\"0\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\"}"
//			+ "{\"tableName\":\"consumer\",\"join\":[\"department\"],\"key\":\"consumer.depId=department.ID\",\"versionColumn\":\"ID\",\"minVersion\":\"0\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\"}"
			+ "]},"
			+ "{\"databaseName\":\"OracleTest\",\"databaseType\":\"oracle\",\"tables\":["
			+ "{\"tableName\":\"users\",\"versionColumn\":\"ID\",\"minVersion\":\"0\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\"},"
			+ "{\"tableName\":\"student\",\"join\":[\"department\"],\"key\":\"student.depId=department.ID\",\"versionColumn\":\"ID\",\"minVersion\":\"0\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\"},"
			+ "{\"tableName\":\"course\",\"versionColumn\":\"TOTAL_LINES_NUMBER\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\"},"
			+ "{\"tableName\":\"teacher\",\"join\":[\"department\"],\"key\":\"teacher.depId=department.ID\",\"versionColumn\":\"TOTAL_LINES_NUMBER\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\"},"
			+ "{\"tableName\":\"date_test\",\"join\":[\"department\"],\"key\":\"date_test.depId=department.ID\",\"versionColumn\":\"ID\",\"minVersion\":\"0\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\"},"
			+ "{\"tableName\":\"timestamp_test\",\"join\":[\"department\"],\"key\":\"timestamp_test.depId=department.ID\",\"versionColumn\":\"ID\",\"minVersion\":\"0\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\"}"
//			+ "{\"tableName\":\"consumer\",\"join\":[\"department\"],\"key\":\"consumer.depId=department.ID\",\"versionColumn\":\"ID\",\"minVersion\":\"0\",\"tableUploadUrl\":\"upload://table/bcdfb940-9118-11e6-93c6-d33ad3c1d179/i8DwmCgtyknSFJfW\"}"
			+ "]}"
			+ "]}";


	public static String OldAllDBInitJsonForMockTest = "{\"databases\":["
//			+ "{\"db\":\"MysqlTest\",\"rowversion\":\"ID\",\"tables\":["
//			+ "{\"table\":\"users\",\"ID\":\"0\"},"
//			+ "{\"table\":\"student\",\"ID\":\"0\",\"join\":[\"department\"],\"key\":\"student.depId=department.ID\"},"
//			+ "{\"table\":\"consumer\",\"ID\":\"0\",\"join\":[\"department\"],\"key\":\"consumer.depId=department.ID\"},"
//			+ "{\"table\":\"department\",\"ID\":\"0\",\"join_subtable\":[\"class\"],\"key\":\"department.ID=class.depId\"},"
//			+ "{\"table\":\"monitor\",\"ID\":\"0\",\"replaceOp\":[{\"toColumn\":\"classId\",\"fromTable\":\"class\",\"fromKey\":\"ID\",\"fromColumns\":\"classname\"}]}"
//			+ "]},"
//			+ "{\"db\":\"PostgresqlTest\",\"rowversion\":\"id\",\"tables\":["
////			+ "{\"table\":\"users\",\"id\":\"0\"},"
////			+ "{\"table\":\"student\",\"id\":\"0\",\"join\":[\"department\"],\"key\":\"student.depid=department.id\"},"
////			+ "{\"table\":\"consumer\",\"id\":\"0\",\"join\":[\"department\"],\"key\":\"consumer.depid=department.id\"},"
//			+ "{\"table\":\"department\",\"id\":\"0\",\"join_subtable\":[\"class\"],\"key\":\"department.id=class.depid\"}"
////			+ "{\"table\":\"monitor\",\"id\":\"0\",\"replaceOp\":[{\"toColumn\":\"classid\",\"fromTable\":\"class\",\"fromKey\":\"id\",\"fromColumns\":\"classname\"}]}"
////			+ "]},"
			+ "{\"db\":\"SqlServerTest\",\"rowversion\":\"ID\",\"tables\":["
			+ "{\"table\":\"users\",\"ID\":\"0\"},"
			+ "{\"table\":\"student\",\"ID\":\"0\",\"join\":[\"department\"],\"key\":\"student.depId=department.ID\"},"
		//	+ "{\"table\":\"course\",\"ID\":\"0\",\"join\":[\"department\"],\"key\":\"course.depId=department.ID\"},"
			+ "{\"table\":\"department\",\"ID\":\"0\",\"join_subtable\":[\"class\"],\"key\":\"department.ID=class.depId\"},"
		//	+ "{\"table\":\"datetime_test\",\"ID\":\"0\",\"join\":[\"department\"],\"key\":\"datetime_test.depId=department.ID\"},"
		//	+ "{\"table\":\"teacher\",\"ID\":\"0
		//	+",\"join\":[\"department\"],\"key\":\"teacher.depId=department.ID\",\"versionColumn\":\"TOTAL_LINES_NUMBER\"},"			
			+ "{\"table\":\"monitor\",\"ID\":\"0\",\"replaceOp\":[{\"toColumn\":\"classId\",\"fromTable\":\"class\",\"fromKey\":\"ID\",\"fromColumns\":\"classname\"}]}"
			+ "]}"
			
			//"
//			+ "{\"db\":\"OracleTest\",\"rowversion\":\"ID\",\"tables\":["
//			+ "{\"table\":\"users\",\"ID\":\"0\"},"
//			+ "{\"table\":\"student\",\"ID\":\"0\",\"join\":[\"department\"],\"key\":\"student.depId=department.ID\"},"
//			+ "{\"table\":\"consumer\",\"ID\":\"0\",\"join\":[\"department\"],\"key\":\"consumer.depId=department.ID\"},"
//			+ "{\"table\":\"department\",\"ID\":\"0\",\"join_subtable\":[\"class\"],\"key\":\"department.ID=class.depId\"},"
//			+ "{\"table\":\"monitor\",\"ID\":\"0\",\"replaceOp\":[{\"toColumn\":\"CLASSID\",\"fromTable\":\"class\",\"fromKey\":\"ID\",\"fromColumns\":\"classname\"}]}"
	//		+ "]}"
			//+"
			+ "]}";

//	private static String documentDBInitJson = "{\"databases\":[{\"db\":\"DocumentDB\",\"rowversion\":\"xgsj\",\"tables\":["
//		+ "{\"table\":\"da_jbxx\",\"xgsj\":\"0\"},{\"table\":\"da_jl\",\"xgsj\":\"0\"},{\"table\":\"da_qklj\",\"xgsj\":\"0\"},"
//		+ "{\"table\":\"da_shgx\",\"xgsj\":\"0\"},{\"table\":\"da_tzzb\",\"xgsj\":\"0\"},{\"table\":\"da_tszb\",\"join\":[\"da_tsbc\"],\"key\":\"da_tszb.bh=da_tsbc.bh\",\"xgsj\":\"0\"},"
//        + "{\"table\":\"da_clzl\",\"xgsj\":\"0\"},{\"table\":\"da_crj\",\"xgsj\":\"0\"},{\"table\":\"da_swdj\",\"xgsj\":\"0\"},{\"table\":\"da_tc\",\"xgsj\":\"0\"},"
//        + "{\"table\":\"da_zm\",\"xgsj\":\"0\"},{\"table\":\"yzjc\",\"xgsj\":\"0\"},{\"table\":\"xfzb\",\"xgsj\":\"0\"},{\"table\":\"djbd\",\"xgsj\":\"0\"},"
//        + "{\"table\":\"db\",\"xgsj\":\"0\"},{\"table\":\"nwfg\",\"xgsj\":\"0\"},{\"table\":\"hjdd\",\"xgsj\":\"0\"},{\"table\":\"hjbd\",\"xgsj\":\"0\"},"
//        + "{\"table\":\"jxcd\",\"xgsj\":\"0\"},{\"table\":\"st\",\"xgsj\":\"0\"},{\"table\":\"jy_rzfpbd\",\"xgsj\":\"0\"},{\"table\":\"jwbw\",\"xgsj\":\"0\"},"
//		+ "{\"table\":\"bwxb\",\"xgsj\":\"0\"},{\"table\":\"tt\",\"xgsj\":\"0\"},{\"table\":\"lbc\",\"xgsj\":\"0\"},{\"table\":\"ss\",\"join\":[\"ssfb\"],\"key\":\"ss.ssid=ssfb.ssid\",\"xgsj\":\"0\"},"
//		+ "{\"table\":\"ks\",\"join\":[\"ksfb\"],\"key\":\"ks.bh=ksfb.bh AND ks.ksrq=ksfb.ksrq\",\"xgsj\":\"0\"},{\"table\":\"jfjjzb\",\"xgsj\":\"0\"},{\"table\":\"tbjd\",\"xgsj\":\"0\"},"
//		+ "{\"table\":\"gzjs\",\"xgsj\":\"0\"},{\"table\":\"qjfj\",\"xgsj\":\"0\"},{\"table\":\"yjdj\",\"xgsj\":\"0\"},{\"table\":\"sg\",\"xgsj\":\"0\"},{\"table\":\"em_zb\",\"xgsj\":\"0\"},"
//		+ "{\"table\":\"em_qk\",\"join\":[\"em_zb\"],\"key\":\"em_qk.bh=em_zb.bh\",\"xgsj\":\"0\"},{\"table\":\"em_jd\",\"join\":[\"em_zb\"],\"key\":\"em_jd.bh=em_zb.bh\",\"xgsj\":\"0\"},"
//		+ "{\"table\":\"em_jc\",\"join\":[\"em_zb\"],\"key\":\"em_jc.bh=em_zb.bh\",\"xgsj\":\"0\"},{\"table\":\"em_sy\",\"join\":[\"em_zb\"],\"key\":\"em_sy.bh=em_zb.bh\",\"xgsj\":\"0\"},"
//		+ "{\"table\":\"fpa_zacy\",\"join\":[\"fpa_zb\",\"fpa_swry\"],\"key\":\"fpa_zacy.ah=fpa_zb.ah AND fpa_zacy.ah=fpa_swry.ah\",\"xgsj\":\"0\"},"
//		+ "{\"table\":\"yma_zacy\",\"join\":[\"yma_zb\"],\"key\":\"yma_zacy.ah=yma_zb.ah\",\"xgsj\":\"0\"},{\"table\":\"wjp_bc\",\"join\":[\"wjp_zb\"],\"key\":\"wjp_bc.wjpid=wjp_zb.wjpid\",\"xgsj\":\"0\"},"
//		+ "{\"table\":\"wyld_ry\",\"join\":[\"wyld_zb\"],\"key\":\"wyld_ry.wydid=wyld_zb.wydid\",\"xgsj\":\"0\"},{\"table\":\"hj\",\"join\":[\"hj_fb\"],\"key\":\"hj.hjid=hj_fb.hjid\",\"xgsj\":\"0\"},"
//		+ "{\"table\":\"khjf\",\"xgsj\":\"0\"},{\"table\":\"khjf_sd\",\"xgsj\":\"0\"},{\"table\":\"khf\",\"xgsj\":\"0\"},{\"table\":\"thdj\",\"xgsj\":\"0\"},"
//		+ "{\"table\":\"wp_bgzb\",\"join\":[\"wp_bgbc\"],\"key\":\"wp_bgzb.bh=wp_bgbc.bh AND wp_bgzb.djrq=wp_bgbc.djrq\",\"xgsj\":\"0\"},{\"table\":\"wwzk\",\"xgsj\":\"0\"},"
//		+ "{\"table\":\"wwjc\",\"xgsj\":\"0\"},{\"table\":\"wwbx\",\"join\":[\"wwzk\"],\"key\":\"wwbx.bh=wwzk.bh AND wwbx.pzrq=wwzk.pzrq\",\"xgsj\":\"0\"},{\"table\":\"sndd\",\"xgsj\":\"0\"}]}]}";

//	private static String youDiInitJson = "{\"databases\":[{\"db\":\"MeetingDB\",\"rowversion\":\"starttime\",\"tables\":[{\"table\":\"pias/getItlist\",\"starttime\":\"0\"}]},"
//			+ "{\"db\":\"TalkDB\",\"rowversion\":\"starttime\",\"tables\":[{\"table\":\"qqdh/getTalklist\",\"starttime\":\"0\"},{\"table\":\"qqdh/getQqdh\",\"starttime\":\"0\"}]},"
//			+ "{\"db\":\"PrasDB\",\"rowversion\":\"starttime\",\"tables\":[{\"table\":\"pras/getResult\",\"starttime\":\"0\"},{\"table\":\"pras/getTable\",\"starttime\":\"0\"}]}]}";

	/*
	 * web service db
	 */
	private static String youDiInitJson = "{\"databases\":[{\"db\":\"MeetingDB\",\"rowversion\":\"starttime\",\"tables\":[{\"table\":\"pias/getItlist\",\"starttime\":\"" + fiveDayBefore + "\"}]},"
			+ "{\"db\":\"TalkDB\",\"rowversion\":\"starttime\",\"tables\":[{\"table\":\"qqdh/getTalklist\",\"starttime\":\"" + fiveDayBefore + "\"},{\"table\":\"qqdh/getQqdh\",\"starttime\":\"" + fiveDayBefore + "\"}]},"
			+ "{\"db\":\"PrasDB\",\"rowversion\":\"starttime\",\"tables\":[{\"table\":\"pras/getResult\",\"starttime\":\"" + fiveDayBefore + "\"},{\"table\":\"pras/getTable\",\"starttime\":\"" + fiveDayBefore + "\"}]}]}";

	/*
	 * oracle db
	 */
	private static String zhongCiInitJson = "{\"databases\":[{\"db\":\"JfkhDB\",\"rowversion\":\"ID\",\"tables\":["
			+ "{\"table\":\"BZ_KHBZ_JBSP\",\"ID\":\"0\"},{\"table\":\"BZ_KHBZ_JJJSP\",\"ID\":\"0\"},"
			+ "{\"table\":\"BZ_JFKH_DRECORDSUB\",\"join\":[\"BZ_JFKH_DRECORD\"],\"key\":\"BZ_JFKH_DRECORDSUB.PID=BZ_JFKH_DRECORD.ID\",\"ID\":\"0\"},"
			+ "{\"table\":\"BZ_KHBZ_DOCTOR\",\"join_subtable\":[\"BZ_KHBZ_DOCTORSUB\"],\"key\":\"BZ_KHBZ_DOCTOR.ID=BZ_KHBZ_DOCTORSUB.PID\",\"ID\":\"0\"},"
			+ "{\"table\":\"BZ_JFKH_MYZKJFSPSUB\",\"join\":[\"BZ_JFKH_MYZKJFSP\"],\"key\":\"BZ_JFKH_MYZKJFSPSUB.PID=BZ_JFKH_MYZKJFSP.ID\",\"ID\":\"0\"},"
			+ "{\"table\":\"BZ_JFKH_ZFFJQDDJL\",\"ID\":\"0\"},{\"table\":\"BZ_JFKH_ZFFYDDJL\",\"ID\":\"0\"},"
			+ "{\"table\":\"BZ_KHBZ_LJTQSP\",\"join_subtable\":[\"BZ_KHBZ_LJTQSPSUB\"],\"key\":\"BZ_KHBZ_LJTQSP.ID=BZ_KHBZ_LJTQSPSUB.PID\",\"ID\":\"0\"},"
			+ "{\"table\":\"BZ_KHBZ_TXLJTQSP\",\"ID\":\"0\"},{\"table\":\"BZ_KHBZ_XZCFSP\",\"ID\":\"0\"},{\"table\":\"BZ_KHBZ_XZJLSP\",\"ID\":\"0\"}]}]}";

	/*
	 * sql server 2008
	 */
	private static String bangjiaoInitJson = "{\"databases\":["
			+ "{\"db\":\"VideoMeetingDB\",\"rowversion\":\"ID\",\"tables\":["
				+ "{\"table\":\"MeetingApplies\",\"ID\":\"" + 0 + "\", \"join\":[\"UserAccounts\", \"Prisoner\", \"Users\", \"Departments\", \"Jails\"],"
					+ "\"key\":\" MeetingApplies.PrisonerFk = Prisoner.UserFk and MeetingApplies.CreateUserFk = UserAccounts.Id "
						+ "and Prisoner.UserFk = Users.Id and Users.DepartmentFk = Departments.Id and MeetingApplies.MeetingJailFk = Jails.Id\"}"
				+"]}"

			+ ",{\"db\":\"HelpDB\",\"rowversion\":\"ID\",\"tables\":["
				+ "{\"table\":\"Fee_UserCharges\",\"ID\":\"" + 0 + "\", \"join\":[\"Fee_UserAccounts\", \"Users\", \"Departments\", \"Prisoner\"],"
					+ "\"key\":\"  Fee_UserCharges.UserFk = Users.Id and Fee_UserCharges.UserAccountFk = Fee_UserAccounts.Id "
						+ "and Users.DepartmentFk = Departments.Id and Prisoner.UserFk = Users.Id\"},"
				+ "{\"table\":\"Fee_UserDeductions\",\"ID\":\"" + 0 + "\", \"join\":[\"Users\", \"Departments\", \"Prisoner\"],"
					+ "\"key\":\"Fee_UserDeductions.UserFk = Users.Id and Users.DepartmentFk = Departments.Id "
						+ "and Prisoner.UserFk = Users.Id\"},"
				+ "{\"table\":\"Contacts\",\"ID\":\"" + 0 + "\", \"join\":[\"Prisoner\", \"Users\", \"Departments\", \"CommonCodes\"],"
					+ "\"key\":\"Contacts.PrisonerFk = Prisoner.UserFk and Prisoner.UserFk = Users.Id "
						+ "and Contacts.RelationFk = CommonCodes.Id and Users.DepartmentFk = Departments.Id\"},"
				+ "{\"table\":\"Sms_SmsSendBoxes\",\"ID\":\"" + 0 + "\", \"join\":[\"Prisoner\", \"Users\", \"Departments\", \"CommonCodes\"],"
					+ "\"key\":\"Sms_SmsSendBoxes.PrisonerFk = Prisoner.UserFk and Prisoner.UserFk = Users.Id "
						+ "and Sms_SmsSendBoxes.FailureReasonFk = CommonCodes.Id and Sms_SmsSendBoxes.FeelingFk = CommonCodes.Id "
							+ "and Sms_SmsSendBoxes.RelationFk = CommonCodes.Id and Users.DepartmentFk = Departments.Id\"},"
				+ "{\"table\":\"Sms_SmsReceiveBoxes\",\"ID\":\"" + 0 + "\", \"join\":[\"Prisoner\", \"Users\", \"Departments\", \"CommonCodes\"],"
					+ "\"key\":\"Sms_SmsReceiveBoxes.PrisonerFk = Prisoner.UserFk and Prisoner.UserFk = Users.Id "
						+ "and Sms_SmsReceiveBoxes.FailureReasonFk = CommonCodes.Id and Sms_SmsReceiveBoxes.RelationFk = CommonCodes.Id "
							+ "and Users.DepartmentFk = Departments.Id\"},"
				+ "{\"table\":\"ClassRoom_Histories\",\"ID\":\"" + 0 + "\", \"join\":[\"ClassRoom_Videos\", \"Users\", \"Departments\", \"CommonCodes\", \"Prisoner\"],"
					+ "\"key\":\"ClassRoom_Histories.UserFk = Users.Id and ClassRoom_Histories.VideoFk = ClassRoom_Videos.Id "
						+ "and ClassRoom_Videos.TypeFk = CommonCodes.Id and Users.DepartmentFk = Departments.Id and Prisoner.UserFk = Users.Id\"},"
				+ "{\"table\":\"ClassRoom_StudyNotes\",\"ID\":\"" + 0 + "\", \"join\":[\"ClassRoom_Videos\", \"Users\", \"Departments\", \"CommonCodes\", \"Prisoner\"],"
					+ "\"key\":\"ClassRoom_StudyNotes.UserFk = Users.Id and ClassRoom_StudyNotes.VideoFk = ClassRoom_Videos.Id "
						+ "and ClassRoom_Videos.TypeFk = CommonCodes.Id and Users.DepartmentFk = Departments.Id and Prisoner.UserFk = Users.Id\"},"
				+ "{\"table\":\"Library_BookHistories\",\"ID\":\"" + 0 + "\", \"join\":[\"Library_Books\", \"Users\", \"Departments\", \"CommonCodes\", \"Prisoner\"],"
					+ "\"key\":\"Library_BookHistories.UserFk = Users.Id and Library_BookHistories.BookFk = Library_Books.Id "
						+ "and Library_Books.CatalogFk = CommonCodes.Id and Users.DepartmentFk = Departments.Id and Prisoner.UserFk = Users.Id\"},"
				+ "{\"table\":\"Library_BookReviews\",\"ID\":\"" + 0 + "\", \"join\":[\"Library_Books\", \"Users\", \"Departments\", \"CommonCodes\", \"Prisoner\"],"
					+ "\"key\":\"Library_BookReviews.UserFk = Users.Id and Library_BookReviews.BookFk = Library_Books.Id "
						+ "and Library_Books.CatalogFk = CommonCodes.Id and Users.DepartmentFk = Departments.Id and Prisoner.UserFk = Users.Id\"},"
				+ "{\"table\":\"Vod_VideoHistories\",\"ID\":\"" + 0 + "\", \"join\":[\"Vod_Videos\", \"Users\", \"Departments\", \"CommonCodes\", \"Prisoner\"],"
					+ "\"key\":\"Vod_VideoHistories.UserFk = Users.Id and Vod_VideoHistories.VideoFk = Vod_Videos.Id "
						+ "and Vod_Videos.CatalogFk = CommonCodes.Id and Users.DepartmentFk = Departments.Id and Prisoner.UserFk = Users.Id\"},"
				+ "{\"table\":\"Vod_VideoReviews\",\"ID\":\"" + 0 + "\", \"join\":[\"Vod_Videos\", \"Users\", \"Departments\", \"CommonCodes\", \"Prisoner\"],"
					+ "\"key\":\"Vod_VideoReviews.UserFk = Users.Id and Vod_VideoReviews.VideoFk = Vod_Videos.Id "
						+ "and Vod_Videos.CatalogFk = CommonCodes.Id and Users.DepartmentFk = Departments.Id and Prisoner.UserFk = Users.Id\"},"
				+ "{\"table\":\"Terminal_TerminalApplications\",\"ID\":\"" + 0 + "\", \"join_sub\":[\"Terminal_SubscribeInfos\", \"TerminalApplications_SubscribeInfos\", "
					+ "\"Terminal_TerminalInfos\", \"Terminal_TerminalModels\", \"Users\", \"Departments\", \"Prisoner\"],"
						+ "\"key\":\"Terminal_TerminalApplications.UserFk = Users.Id and Terminal_TerminalApplications.TerminalFk = Terminal_TerminalInfos.Id "
							+ "and Terminal_TerminalApplications.TerminalModelFk = Terminal_TerminalModels.Id and TerminalApplications_SubscribeInfos.SubscribeInfoFk = Terminal_SubscribeInfos.Id "
								+ "and TerminalApplications_SubscribeInfos.TerminalApplicationFk = Terminal_TerminalApplications.Id and Users.DepartmentFk = Departments.Id and Prisoner.UserFk = Users.Id\"},"
				+ "{\"table\":\"Messages\",\"ID\":\"" + 0 + "\", \"join\":[\"Users\", \"Departments\", \"Prisoner\"],"
					+ "\"key\":\"Messages.UserFk = Users.Id and Users.DepartmentFk = Departments.Id and Prisoner.UserFk = Users.Id\"},"
				+ "{\"table\":\"RainGlass_UserEmotions\",\"ID\":\"" + 0 + "\", \"join\":[\"RainGlass_Emotions\", \"RainGlass_EmotionPersuasions\", \"Users\", \"Departments\", \"Prisoner\"],"
					+ "\"key\":\"RainGlass_UserEmotions.UserFk = Users.Id and RainGlass_UserEmotions.EmotionFk = RainGlass_Emotions.Id "
						+ "and RainGlass_Emotions.Id = RainGlass_EmotionPersuasions.EmotionFk and Users.DepartmentFk = Departments.Id and Prisoner.UserFk = Users.Id\"},"
				+ "{\"table\":\"MusicAppre_PlayHistories\",\"ID\":\"" + 0 + "\", \"join\":[\"MusicAppre_Musics\", \"Users\", \"Departments\", \"CommonCodes\", \"Prisoner\"],"
					+ "\"key\":\"MusicAppre_PlayHistories.MusicFk = MusicAppre_Musics.Id and MusicAppre_PlayHistories.UserFk = Users.Id "
						+ "and Users.DepartmentFk = Departments.Id and MusicAppre_Musics.CatalogFk = CommonCodes.Id and Prisoner.UserFk = Users.Id\"}"
				+ "]}"
			+ "]}";

	/*
	 * file db
	 */
	private static String documentFilesInitJson = "{\"databases\":[{\"db\":\"DocumentFiles\",\"rowversion\":\"filetime\",\"tables\":"
			+ "[{\"table\":\"" + PROJECT_ABSOLUTE_PATH + "/src/resources/fileUploaderTestPics\",\"xgsj\":\"0000000000000000\"}]}]}";

	public static String getTableId() {
		return clientId;
	}

	public static MultiDatabaseBean getMultiDatabaseBean() throws JsonParseException, JsonMappingException, IOException{
		if(databaseBeans == null && clientId != null){
			ObjectMapper oMapper = new ObjectMapper();			
			//test all
//				databaseBeans = oMapper.readValue(AllDBInitJson, MultiDatabaseBean.class);
			//DocumentDB's xgsj change to 5 
//				databaseBeans = oMapper.readValue(changeDocumentDBXgsjInitJson, MultiDatabaseBean.class);
			//for web server test
//				databaseBeans = oMapper.readValue(youDiInitJson, MultiDatabaseBean.class);
			//for sqlite
//				databaseBeans = oMapper.readValue(documentDBForSqliteInitJson, MultiDatabaseBean.class);
			//for oracle
//				databaseBeans = oMapper.readValue(zhongCiInitJson, MultiDatabaseBean.class);
			//for sql server2008
//				databaseBeans = oMapper.readValue(bangjiaoInitJson, MultiDatabaseBean.class);
//			test mock db
			databaseBeans = oMapper.readValue(AllDBInitJsonForMockTest, MultiDatabaseBean.class);
			//test file
//			databaseBeans = oMapper.readValue(documentFilesInitJson, MultiDatabaseBean.class);

		}
		return databaseBeans;
	}

	public static void setMultiDatabaseBean(MultiDatabaseBean dbs) {
		databaseBeans = dbs;
	}

	public static String getDBInitJsonForTest(){
//		return AllDBInitJsonForMockTest;
		return OldAllDBInitJsonForMockTest;
	}

	public static void setDBInitJsonForTest(CheckAndAppendTestConf checkAndAppendBean){
		if(checkAndAppendBean.isNewVersion()){
			AllDBInitJsonForMockTest = getNewDBInitJsonForTest(checkAndAppendBean);
		} else {
			AllDBInitJsonForMockTest = getOldDBInitJsonForTest(checkAndAppendBean);
		}
	}

    private static String getOldDBInitJsonForTest(CheckAndAppendTestConf checkAndAppendBean) {
    	JSONArray dbArray = new JSONArray();
    	for(DatabaseBeanTestConf dbBean : checkAndAppendBean.getDatabases()){
    		JSONObject dbObject = new JSONObject();
    		dbObject.put("db", dbBean.getDatabaseName());
    		JSONArray tArray = new JSONArray();
    		for(TableBeanTestConf tBean : dbBean.getTables()){
    			if(tBean.getVersionColumn().equals("TOTAL_LINES_NUMBER")){
    				continue;
    			}
    			if(!tBean.getVersionColumn().equals("TOTAL_LINES_NUMBER") && !dbObject.containsKey("rowversion")){
    				dbObject.put("rowversion", tBean.getVersionColumn());
    			}
    			JSONObject tObject = new JSONObject();
    			tObject.put("table", tBean.getTableName());
    			switch (tBean.getVersionColumn()) {
				case "xgsj":
					tObject.put("xgsj", tBean.getMinVersion());
					break;
				case "starttime":
					tObject.put("starttime", tBean.getMinVersion());
					break;
				case "ID":
					tObject.put("ID", tBean.getMinVersion());
					break;
				case "OPTIME":
					tObject.put("OPTIME", tBean.getMinVersion());
					break;
				case "MDATE":
					tObject.put("MDATE", tBean.getMinVersion());
					break;
				case "FLOWSN":
					tObject.put("FLOWSN", tBean.getMinVersion());
					break;
				default:
					break;
				}
    			if(tBean.getJoin() != null){
    				tObject.put("join", tBean.getJoin());
    			}
    			if(tBean.getKey() != null){
    				tObject.put("key", tBean.getKey());
    			}
    			if(tBean.getJoin_subtable() != null){
    				tObject.put("join_subtable", tBean.getJoin_subtable());
    			}
    			if(tBean.getReplaceOp() != null && tBean.getReplaceOp().size() > 0){
    				tObject.put("replaceOp", tBean.getReplaceOp());
    			}
    			tArray.add(tObject);
    		}
    		dbObject.put("tables", tArray);
    		dbArray.add(dbObject);
    	}
    	JSONObject dbs = new JSONObject();
    	dbs.put("databases", dbArray);
		return dbs.toString();
	}

	private static String getNewDBInitJsonForTest(CheckAndAppendTestConf checkAndAppendBean) {
		JSONArray dbArray = new JSONArray();
    	for(DatabaseBeanTestConf dbBean : checkAndAppendBean.getDatabases()){
    		JSONObject dbObject = new JSONObject();
    		dbObject.put("databaseName", dbBean.getDatabaseName());
    		dbObject.put("databaseType", dbBean.getDatabaseType());
    		BasicDataSource bSource = (BasicDataSource) dbBean.getJdbcTemplate().getDataSource();
    		JSONObject jObject = new JSONObject();
    		jObject.put("driverClassName", bSource.getDriverClassName());
    		jObject.put("url", bSource.getUrl());
    		jObject.put("username", bSource.getUsername());
    		jObject.put("password", bSource.getPassword());
    		dbObject.put("jdbcTemplate", jObject.toString());
    		JSONArray tArray = new JSONArray();
    		for(TableBeanTestConf tBean : dbBean.getTables()){
    			JSONObject tObject = new JSONObject();
    			tObject.put("tableName", tBean.getTableName());
    			tObject.put("tableUploadUrl", tBean.getTableUploadUrl());
				tObject.put("versionColumn", tBean.getVersionColumn());
				if(!tBean.getVersionColumn().equals("TOTAL_LINES_NUMBER")){
					tObject.put("minVersion", tBean.getMinVersion());
				}
				if(tBean.getJoin() != null){
					tObject.put("join", tBean.getJoin());
				}
				if(tBean.getKey() != null){
					tObject.put("key", tBean.getKey());
				}
				if(tBean.getJoin_subtable() != null){
					tObject.put("join_subtable", tBean.getJoin_subtable());
				}
				if(tBean.getReplaceOp() != null){
					tObject.put("replaceOp", tBean.getReplaceOp());
				}
    			tArray.add(tObject);
    		}
    		dbObject.put("tables", tArray);
    		dbArray.add(dbObject);
    	}
    	JSONObject dbs = new JSONObject();
    	dbs.put("databases", dbArray);
		return dbs.toString();
	}

	@Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
    	//logger.info(AllDBInitJsonForMockTest);
    	String url = req.getRequestURI();
    	int tableIdIndex = url.lastIndexOf('/');
    	if (url.length() < getTaskApi.length() || tableIdIndex != (getTaskApi.length() - 1)) {
			throw new ServletException("invalid url, format: " + getTaskApi + "{tableId}");
		}

    	System.out.println("start get task succeed!");

    	clientId = url.substring(tableIdIndex + 1);
    	String json = getDBInitJsonForTest();
//    	databaseBeans = getMultiDatabaseBean();    	
//		ObjectMapper oMapper = new ObjectMapper();
//		json = oMapper.writeValueAsString(databaseBeans);
//		logger.info("task from server："+json);

    	//resp.setHeader(\"Content-type\", \"text/html;charset=UTF-8\");
    	resp.setCharacterEncoding("utf-8");
    	PrintWriter pw = resp.getWriter();
    	//pw.write(tableId);
        pw.write(json);
       // System.out.println("json is "+json);
        pw.flush();
        pw.close();
       // System.out.println("get task succeed!");
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	super.doDelete(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	super.doPut(req, resp);
    }
    
    public static void main(String []args) {
		System.out.println(fourDayBeforeString);
		System.out.println(fourDayBefore);
		System.out.println(fiveDayBefore);
		System.out.println(PROJECT_ABSOLUTE_PATH);
	}
}
