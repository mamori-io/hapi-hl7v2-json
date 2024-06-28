package io.mamori.hl7v2;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.MessageVisitors;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.validation.builder.support.NoValidationBuilder;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

/**
 * Parse and convert sample messages to json.
 */
public class VisitorTest {


    @Test
    public void testSIU_S12() throws Exception {
        HapiContext context = new DefaultHapiContext();
        String siu = "MSH|^~\\&|MESA_OP|XYZ_HOSPITAL|iFW|ABC_HOSPITAL|20110613061611||SIU^S12|24916560|P|2.3||||||\r" +
                "SCH|10345^10345|2196178^2196178|||10345|OFFICE^Office visit|reason for the appointment|OFFICE|60|m|^^60^20110617084500^20110617093000|||||9^DENT^ARTHUR^||||9^DENT^COREY^|||||Scheduled\r" +
                "PID|1||42||SMITH^PAUL||19781012|M|||1 Broadway Ave^^Fort Wayne^IN^46804||(260)555-1234|||S||999999999|||||||||||||||||||||\r" +
                "PV1|1|O|||||1^Smith^Miranda^A^MD^^^^|2^Withers^Peter^D^MD^^^^||||||||||||||||||||||||||||||||||||||||||99158||\r" +
                "RGS|1|A\r" +
                "AIG|1|A|1^White, Charles|D^^\r" +
                "AIL|1|A|OFFICE^^^OFFICE|^Main Office||20110614084500|||45|m^Minutes||Scheduled\r" +
                "AIP|1|A|1^White^Charles^A^MD^^^^|D^White, Douglas||20110614084500|||45|m^Minutes||Scheduled";

        context.setValidationRuleBuilder(new NoValidationBuilder());
        Parser p = context.getPipeParser();

        Message m = p.parse(siu);
        JSONVisitor visitor = new JSONVisitor(m.getName(), m.getVersion());
        MessageVisitors.visit(m, visitor);
        JSONObject json = visitor.getPayload();

        System.out.println(json.toString(2));
    }

    @Test
    public void testSIU_S15() throws Exception {
        HapiContext context = new DefaultHapiContext();
        String siu = "MSH|^~\\&|ATHENANET|99999^MA - athenahealth Testing^1^Test Clinic|TestInterface||201505181416||SIU^S15|189M90009|T|2.3.1\r" +
                "SCH|33910|33910||||22^CAR PROBLEMS|Test Appointment|RWFU^RWMA-FOLLOWUP|15|minutes|^^^201505191300|||||username|||||||||NOSHOW\r" +
                "PID||105613|105613|105613|LASTNAME^FIRSTNAME^MIDDLENNAME^SUFFIX||19880601|M|PREFERREDNAME|2028-9^Asian|ADDRESS^ADDRESS (CTD)^CITY^STATE^00000^COUNTRY||(111)111-1111^PRN^PH^^1^111^1111111~(333)333-3333^WPN^PH^^1^333^3333333~Patientemail@email.com^NET^^Patientemail@email.com~(222)222-2222^ORN^CP^^1^222^2222222|(333)333-3333^WPN^PH^^1^333^3333333|eng^English|S|||000000000||ATHENA|2186-5^Not Hispanic or Latino\r" +
                "PD1||||A12123^PCPLASTNAME^PCPFIRSTNAME\r" +
                "PV1|||2^TEST DEPARTMENT^^TEST DEPARTMENT||||12345678^SEUSS^DOCTOR||||||||||12345678^SEUSS^DOCTOR||33910|||||||||||||||||||||||||||||||33910\r" +
                "AIG|||providerusername|||||201505191300|||15|minutes\r" +
                "AIL|||2^TEST DEPARTMENT|||201505191300|||15|minutes\r";

        context.setValidationRuleBuilder(new NoValidationBuilder());
        Parser p = context.getPipeParser();

        Message m = p.parse(siu);
        JSONVisitor visitor = new JSONVisitor(m.getName(), m.getVersion());
        MessageVisitors.visit(m, visitor);
        JSONObject json = visitor.getPayload();

        System.out.println(json.toString(2));
    }

}
