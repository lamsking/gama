package msi.gaml.architecture.simplebdi;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.statements.AbstractStatement;
import msi.gaml.types.IType;

@symbol(name = EnforcementStatement.ENFORCEMENT, kind = ISymbolKind.SINGLE_STATEMENT, with_sequence = false, concept = {
		IConcept.BDI })
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT })
@facets(value = {
		@facet(name = IKeyword.NAME, type = IType.ID, optional = true, doc = @doc("the identifier of the enforcement")),
		@facet(name = IKeyword.WHEN, type = IType.BOOL, optional = true, doc = @doc("A boolean value to enforce only with a certain condition")),
		@facet (
				name = EnforcementStatement.NORM,
				type = IType.STRING,
				optional = true,
				doc = @doc ("The norm to enforce")),
		@facet (
				name = EnforcementStatement.OBLIGATION,
				type = PredicateType.id,
				optional = true,
				doc = @doc ("The obligation to enforce")),
		@facet (
				name = EnforcementStatement.SANCTION,
				type = IType.STRING,
				optional = true,
				doc = @doc ("The sanction to apply if the norm is violated")),
		@facet (
				name = EnforcementStatement.REWARD,
				type = IType.STRING,
				optional = true,
				doc = @doc ("The positive sanction to apply if the norm has been followed"))}
		)
@doc(value = "enables to directly add a belief from the variable of a perceived specie.", examples = {
		@example("focus var:speed /*where speed is a variable from a species that is being perceived*/") })

//statement servant à controler les normes pour appliquer des sanctions, sur le moodèle du focus
public class EnforcementStatement extends AbstractStatement{

	public static final String ENFORCEMENT = "enforcement";
	public static final String NORM = "norm";
	public static final String SANCTION = "sanction";
	public static final String REWARD = "reward";
	public static final String OBLIGATION = "obligation";

	final IExpression name;
	final IExpression when;
	final IExpression norm;
	final IExpression sanction;
	final IExpression reward;
	final IExpression obligation;
	
	public EnforcementStatement(IDescription desc) {
	super(desc);
	name = getFacet(IKeyword.NAME);
	when = getFacet(IKeyword.WHEN);
	norm = getFacet(EnforcementStatement.NORM);
	sanction = getFacet(EnforcementStatement.SANCTION);
	reward = getFacet(EnforcementStatement.REWARD);
	obligation = getFacet(EnforcementStatement.OBLIGATION);
	}

	@Override
	protected Object privateExecuteIn(IScope scope) throws GamaRuntimeException {
		Object retour = null;
		if (when == null || Cast.asBool(scope, when.value(scope))) {
			final IAgent[] stack = scope.getAgentsStack();
			final IAgent mySelfAgent = stack[stack.length - 2];
			IScope scopeMySelf = null;
			if (mySelfAgent != null) {
				scopeMySelf = mySelfAgent.getScope().copy("in EnforcementStatement");
				scopeMySelf.push(mySelfAgent);
			}
			if(norm!=null){
				//on recherche la norme avec le même nom chez l'autre et on regarde si elle est violée
				Norm normToTest = null;
				//Améliorable en temps de calcul
				for(Norm tempNorm : SimpleBdiArchitecture.getNorms(scope)){
					if(tempNorm.getName().equals(norm.value(scopeMySelf))){
						normToTest = tempNorm;
					}
				}
				if(normToTest.getViolated() && sanction!=null){
					//On applique la sanction
					Sanction sanctionToExecute = null;
					//Améliorable en temps de calcul
					for(Sanction tempSanction : SimpleBdiArchitecture.getSanctions(scopeMySelf)){
						if(tempSanction.getName().equals(sanction.value(scopeMySelf))){
							sanctionToExecute = tempSanction;
						}
					}
					//Ici, la sanction est exécutée dans le contexte de l'agent controleur car la sanction est indirecte contre une norme sociale
//					return sanctionToExecute.getSanctionStatement().executeOn(scopeMySelf);
					retour = sanctionToExecute.getSanctionStatement().executeOn(scopeMySelf);
				} else if(!normToTest.getViolated() && reward!=null){
					//on applique le reward
					Sanction rewardToExecute = null;
					//Améliorable en temps de calcul
					for(Sanction tempReward : SimpleBdiArchitecture.getSanctions(scopeMySelf)){
						if(tempReward.getName().equals(reward.value(scopeMySelf))){
							rewardToExecute = tempReward;
						}
					}
					//Ici, le reward est exécuté dans le contexte de l'agent controleur car la sanction est indirecte contre une norme sociale
//					return rewardToExecute.getSanctionStatement().executeOn(scopeMySelf);
					retour = rewardToExecute.getSanctionStatement().executeOn(scopeMySelf);
				}
			}
			if(obligation!=null){
				//on regarde si la base des obligations de l'autre est vide, si non, on regarde s'il a appliqué une norme portant sur l'obligation à vérifiée.
				//Les sanctions et rewards seront ici appliquées dans le cadre de l'agent controlé car directe
				MentalState tempObligation = new MentalState("obligation",(Predicate)obligation.value(scope));
				if(SimpleBdiArchitecture.hasObligation(scope,tempObligation)){
					//si ma norme en cours répond à l'iobligation , reward, sinon punition.
					Norm tempNorm = new Norm ((NormStatement)scope.getAgent().getAttribute("current_norm"));
					if(reward!=null && tempNorm!=null && tempNorm.getObligation(scope)!=null && tempNorm.getObligation(scope).equals(tempObligation.getPredicate())){
						Sanction rewardToExecute = null;
						//Améliorable en temps de calcul
						for(Sanction tempReward : SimpleBdiArchitecture.getSanctions(scopeMySelf)){
							if(tempReward.getName().equals(reward.value(scopeMySelf))){
								rewardToExecute = tempReward;
							}
						}
						//Ici, le reward est exécuté dans le contexte de l'agent controleur car la sanction est indirecte contre une norme sociale
//						return rewardToExecute.getSanctionStatement().executeOn(scopeMySelf);
						retour = rewardToExecute.getSanctionStatement().executeOn(scopeMySelf);
					}
					if(sanction!=null && tempNorm!=null && tempNorm.getObligation(scope)!=null && !tempNorm.getObligation(scope).equals(tempObligation.getPredicate())){
						Sanction sanctionToExecute = null;
						//Améliorable en temps de calcul
						for(Sanction tempSanction : SimpleBdiArchitecture.getSanctions(scopeMySelf)){
							if(tempSanction.getName().equals(sanction.value(scopeMySelf))){
								sanctionToExecute = tempSanction;
							}
						}
						//Ici, le reward est exécuté dans le contexte de l'agent controleur car la sanction est indirecte contre une norme sociale
//						return sanctionToExecute.getSanctionStatement().executeOn(scopeMySelf);
						retour = sanctionToExecute.getSanctionStatement().executeOn(scopeMySelf);
					}
				}
			}
			GAMA.releaseScope(scopeMySelf);
		}
		return retour;
	}

}