package participation;


import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import mission.MissionDTO;


@Controller
public class ParticipationController {
	
	@Autowired
	@Qualifier("participationservice")
	ParticipationService service = new ParticipationServiceImpl();
	
	
	 @RequestMapping("/missioncomplete")
	 public String missioncomplete(){			 
			return "/mypage/missioncomplete";
	 }
	 
	//나의 미션 목록
	 @RequestMapping(value= {"/participation", "/participation2"})
     public ModelAndView list(HttpSession session, ModelAndView mv) {

		Map<String, Object> map = new HashMap<>();
     	
     	String id=(String)session.getAttribute("session_id");
    
     	if(id!=null) { 
     		java.util.List<ParticipationDTO> list = service.participation_list(id);
     		 
             map.put("list", list);
             map.put("count", list.size()); 
             
             mv.setViewName("mypage/participation"); 
             mv.addObject("map", map); 
             
             return mv;
     	}else{
     		return new ModelAndView("member/login", "", null);
     		
     	}
	}
	
	 //미션 등록하기
	  @RequestMapping("/register") 
	  public String register(@ModelAttribute ParticipationDTO dto, HttpSession session) { // 사용자 id를 받아옴 
		  String id = (String)session.getAttribute("session_id"); 
	  if (id == null) {
	  return "member/login"; 
	  } 
	  dto.setId(id); 
	  service.participation_register(dto); 
	  service.participation_register2(dto); //미션 테이블 수정(참가인원)
	 return "redirect:/participation"; 
	  }
	  
	//미션 취소하기
		@RequestMapping("delete")
	    public String delete(@RequestParam int p_code, ParticipationDTO dto) {
			service.delete(p_code);
			service.participation_delete2(dto); //미션 테이블 수정(참가인원)
	        return "redirect:/participation";
	    }
		
		
		//미션 완료하기
		@RequestMapping("/complete")
	    public String comlete(ParticipationDTO dto) throws Exception {
			MultipartFile mf = dto.getImage();
			
			if(!mf.isEmpty()) {			
				Path currentPath = Paths.get(""); 
				String path = currentPath.toAbsolutePath().toString() + "/src/main/resources/static/img/"; 
				path = path.replace("\\", "/");
				File serverfile = new File(path + mf.getOriginalFilename());
				mf.transferTo(serverfile);
				dto.setP_photo(mf.getOriginalFilename());
			}
			
			service.participation_complete(dto); //나의미션 테이블 수정(미션완료, 인증사진 업로드)
			service.participation_complete2(dto); //멤버 테이블 수정(포인트, 탄소배출량)
	        return "redirect:/participation";
	    }
		//미션 리뷰쓰기
		@RequestMapping("/review")
		public String review(ParticipationDTO dto) {						
			service.participation_review(dto); //리뷰제출
			return "redirect:/participation";
		}
		 //미션 리뷰쓰기 모달창
			/*
			 * @RequestMapping("/mymissiondetail2")
			 * 
			 * @ResponseBody public ParticipationDTO mymission_review(int p_code) { return
			 * service.mymission_detail(p_code); }
			 */
		
		
		
	 //미션 인증하기 모달창
		@RequestMapping("/mymissiondetail")
		@ResponseBody
		public ParticipationDTO mymission_modal(int p_code) {
			return service.mymission_detail(p_code);
		}
		


}//Controller end