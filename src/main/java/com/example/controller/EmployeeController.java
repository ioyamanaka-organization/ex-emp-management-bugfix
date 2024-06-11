package com.example.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.example.form.SearchNameForm;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.domain.Employee;
import com.example.form.UpdateEmployeeForm;
import com.example.service.EmployeeService;

/**
 * 従業員情報を操作するコントローラー.
 * 
 * @author igamasayuki
 *
 */
@Controller
@RequestMapping("/employee")
public class EmployeeController {

	@Autowired
	private EmployeeService employeeService;

	/**
	 * 使用するフォームオブジェクトをリクエストスコープに格納する.
	 * 
	 * @return フォーム
	 */
	@ModelAttribute
	public UpdateEmployeeForm setUpForm() {
		return new UpdateEmployeeForm();
	}

	/////////////////////////////////////////////////////
	// ユースケース：従業員一覧を表示する
	/////////////////////////////////////////////////////
	/**
	 * 従業員一覧画面または、あいまい検索の結果を出力します.
	 *
	 * @param form 従業員の名前が入ったフォーム
	 * @param model モデル
	 * @param request リクエスト
	 * @return 従業員一覧画面
	 */
	@GetMapping("/showList")
	public String showList(SearchNameForm form, Model model, HttpServletRequest request, Integer page) {

//		全件表示時のpageのリスト
		List<Employee> employees = employeeService.showList(null,null);
		int pages = (int) Math.ceil((double)employees.size() / 10);
		if (page == null){page = 1;}
		List<Integer> pageList = new ArrayList<>();
		// 1からページ数までの数字をリストに追加
		for (int i = 1; i <= pages; i++) {
			pageList.add(i);
		}

		// 初期表示
		if (form.getName() == null) {
			Integer offset = (page - 1) * 10;
			List<Employee> employeeList = employeeService.showList(null,offset);
			model.addAttribute("pageList", pageList);
			model.addAttribute("employeeList", employeeList);
		} else {
			// 初期表示じゃないとき
			List<Employee> employeeList = employeeService.showList(form.getName(),null);
			// 検索結果が0件の時
			if (employeeList.isEmpty()) {
				// nameに値が入っていた時
				if (request.getParameterMap().containsKey("name")){
					model.addAttribute("errorEmpty", "１件もありませんでした");
				}
				Integer offset = (page - 1) * 10;
				employeeList = employeeService.showList(null,offset);
				model.addAttribute("pageList", pageList);
				model.addAttribute("employeeList", employeeList);
			} else {
				//検索結果があった時
				int resultPages = (int) Math.ceil((double)employeeList.size() / 10);
				pageList = new ArrayList<>();
				// 1からページ数までの数字をリストに追加
				for (int i = 1; i <= resultPages; i++) {
					pageList.add(i);
				}
				Integer offset = (page - 1) * 10;
				employeeList = employeeService.showList(form.getName(),offset);
				model.addAttribute("pageList", pageList);
				model.addAttribute("employeeList", employeeList);
			}
		}
		return "employee/list";
	}

	/////////////////////////////////////////////////////
	// ユースケース：従業員詳細を表示する
	/////////////////////////////////////////////////////
	/**
	 * 従業員詳細画面を出力します.
	 * 
	 * @param id    リクエストパラメータで送られてくる従業員ID
	 * @param model モデル
	 * @return 従業員詳細画面
	 */
	@GetMapping("/showDetail")
	public String showDetail(String id, Model model) {
		Employee employee = employeeService.showDetail(Integer.parseInt(id));
		model.addAttribute("employee", employee);
		return "employee/detail";
	}

	/////////////////////////////////////////////////////
	// ユースケース：従業員詳細を更新する
	/////////////////////////////////////////////////////
	/**
	 * 従業員詳細(ここでは扶養人数のみ)を更新します.
	 * 
	 * @param form 従業員情報用フォーム
	 * @return 従業員一覧画面へリダクレクト
	 */
	@PostMapping("/update")
	public String update(@Validated UpdateEmployeeForm form, BindingResult result, Model model) {
		if (result.hasErrors()) {
			return showDetail(form.getId(), model);
		}
		Employee employee = new Employee();
		employee.setId(form.getIntId());
		employee.setDependentsCount(form.getIntDependentsCount());
		employeeService.update(employee);
		return "redirect:/employee/showList";
	}


}
