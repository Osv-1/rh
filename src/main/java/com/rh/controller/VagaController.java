package com.rh.controller;


import com.rh.model.Candidato;
import com.rh.model.Vaga;
import com.rh.repository.CandidatoRepository;
import com.rh.repository.VagaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
public class VagaController {

    @Autowired
    private VagaRepository vr;
    @Autowired
    private CandidatoRepository cr;


    //cadastrar vaga
    @RequestMapping(value = "/cadastrarVaga", method = RequestMethod.GET)
    public String form() {

        return "vaga/formVaga";

    }

    @RequestMapping(value = "/cadastrarVaga", method = RequestMethod.POST)
    public String form(@Valid Vaga vaga, BindingResult result, RedirectAttributes attributes) {


        if (result.hasErrors()) {
            attributes.addFlashAttribute("mensagem", "Verifique os campos...");
            return "redirect:/cadastrarVaga";
        }
        vr.save(vaga);
        attributes.addFlashAttribute("mensagem", "Vaga cadastrada com sucesso !");
        return "redirect:/cadastrarVaga";

    }

    //Listar vagas

    @RequestMapping(value = "/vagas")
    public ModelAndView listaVaga() {
        ModelAndView mv = new ModelAndView("vaga/listaVaga");
        Iterable<Vaga> vagas = vr.findAll();
        mv.addObject("vagas", vagas);
        return mv;

    }

    @RequestMapping(value = "/{codigo}", method = RequestMethod.GET)
    public ModelAndView detalhesVaga(@PathVariable("codigo") long codigo) {
        Vaga vaga = vr.findByCodigo(codigo);

        ModelAndView mv = new ModelAndView("vaga/detalhesVaga");
        mv.addObject("vaga", vaga);
        Iterable<Candidato> candidatos = cr.findByVaga(vaga);
        mv.addObject("candidatos", candidatos);
        return mv;
    }

    //deletar vaga

    @RequestMapping("/deletarVaga")
    public String deletarVaga(long codigo) {
        Vaga vaga = vr.findByCodigo(codigo);
        vr.delete(vaga);
        return "redirect:/vagas";

    }


    //Detalhes da vaga e salvar candidato
    @RequestMapping(value = "/{codigo}", method = RequestMethod.POST)
    public String detalhesVagaPost(@PathVariable("codigo") long codigo, @Valid Candidato candidato,
                                   BindingResult result, RedirectAttributes attributes) {

        if (result.hasErrors()) {
            attributes.addFlashAttribute("mensagem", "Verifique os campos !");
            return "redirect:/{codigo}";
        }

        //Rg duplicado
        if (cr.findByRg(candidato.getRg()) != null) {
            attributes.addFlashAttribute("mensagem_erro", "Rg duplicado");
            return "redirect:/{codigo}";

        }
        Vaga vaga = vr.findByCodigo(codigo);
        candidato.setVaga(vaga);
        cr.save(candidato);
        attributes.addFlashAttribute("mensagem", "Candidato adicionado com sucesso !");
        return "redirect:/{codigo}";
    }

    //Deleta candidato


    @RequestMapping("/deletarCandidato")
    public String deletarCandidato(String rg) {
        Candidato candidato = cr.findByRg(rg);
        Vaga vaga = candidato.getVaga();
        String codigo = "" + vaga.getCodigo();

        cr.delete(candidato);

        return "redirect:/" + codigo;

    }

    //formulario vaga

    @RequestMapping(value = "/editar-vaga", method = RequestMethod.GET)
    public ModelAndView editarVaga(long codigo) {
        Vaga vaga = vr.findByCodigo(codigo);
        ModelAndView mv = new ModelAndView("vaga/update-vaga");
        mv.addObject("vaga", vaga);
        return mv;

    }

    //update

    @RequestMapping(value = "/editar-vaga", method = RequestMethod.POST)
    public String updateVaga(@Valid Vaga vaga, BindingResult result, RedirectAttributes attributes) {

        vr.save(vaga);
        attributes.addFlashAttribute("Sucess", "Vaga alterada com sucesso !");

        //Url de retorno
        long codigoLong = vaga.getCodigo();
        String codigo = "" + codigoLong;
        return "redirect:/" + codigo;


    }


}
