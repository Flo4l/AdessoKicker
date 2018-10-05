package de.adesso.kicker.tournament.singleelimination;

import de.adesso.kicker.tournament.TournamentController;
import de.adesso.kicker.tournament.TournamentFormats;
import de.adesso.kicker.tournament.TournamentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
public class SingleEliminationController extends TournamentController {

    private TournamentService tournamentService;

    public SingleEliminationController(TournamentService tournamentService) {

        this.tournamentService = tournamentService;
    }
    @GetMapping("/tournaments/create/singleelimination")
    public ModelAndView singleEliminationForm() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("singleElimination", new SingleElimination());
        modelAndView.setViewName("tournament/createsingleelimination");
        return modelAndView;
    }

    @PostMapping("/tournaments/create/singleelimination")
    public ModelAndView createSingleElimination(@Valid SingleElimination singleElimination, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        ModelAndView modelAndView = new ModelAndView();
        if (bindingResult.hasErrors()) {
            modelAndView.addObject("singleelimination", new SingleElimination());
            modelAndView.setViewName("tournament/createsingleelimination");
            return modelAndView;
        } else { tournamentService.saveTournament(singleElimination);
            redirectAttributes.addFlashAttribute("successMessage", "Tournament has been created");
            redirectAttributes.addFlashAttribute("tournamentFormats", TournamentFormats.values());
            modelAndView.setViewName("redirect:/tournaments/create");
        }
        return modelAndView;
    }
}
